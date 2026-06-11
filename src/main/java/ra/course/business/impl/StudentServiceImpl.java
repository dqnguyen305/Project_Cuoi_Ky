package ra.course.business.impl;

import ra.course.business.IStudentService;
import ra.course.dao.IStudentDAO;
import ra.course.dao.IEnrollmentDAO;
import ra.course.dao.impl.StudentDAOImpl;
import ra.course.dao.impl.EnrollmentDAOImpl;
import ra.course.model.Student;

import java.text.SimpleDateFormat;
import java.util.List;

public class StudentServiceImpl implements IStudentService {
    // DAO quản lý thông tin cốt lõi của Học viên
    private final IStudentDAO studentDAO = new StudentDAOImpl();

    // DAO quản lý riêng việc Đăng ký khóa học (Bảng enrollment)
    private final IEnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();

    @Override
    public Student loginStudent(String email, String password) {
        return studentDAO.loginStudent(email, password);
    }

    @Override
    public List<Student> getAllStudents(String sortBy, String direction) {
        return studentDAO.getAllStudents(sortBy, direction);
    }

    @Override
    public boolean addStudent(Student student) {
        return studentDAO.addStudent(student);
    }

    @Override
    public boolean updateStudentField(int id, String fieldName, Object value) {
        return studentDAO.updateStudentField(id, fieldName, value);
    }

    @Override
    public boolean deleteStudent(int id) {
        return studentDAO.deleteStudent(id);
    }

    @Override
    public List<Student> searchStudent(String keyword) {
        return studentDAO.searchStudent(keyword);
    }

    @Override
    public boolean changePassword(int id, String oldPassword, String emailOrPhone, String newPassword) {
        // Lấy thông tin học viên từ DB lên để kiểm tra bảo mật
        Student studentInDb = studentDAO.getAllStudents(null, "asc")
                .stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);

        if (studentInDb == null) {
            System.out.println("\n Lỗi: Không tìm thấy thông tin tài khoản học viên!");
            return false;
        }

        // Kiểm tra mật khẩu cũ
        if (!studentInDb.getPassword().equals(oldPassword)) {
            System.out.println("\n Lỗi: Mật khẩu cũ nhập vào không chính xác!");
            return false;
        }

        // Kiểm tra Email hoặc Số điện thoại xác thực
        boolean isEmailMatch = studentInDb.getEmail().equalsIgnoreCase(emailOrPhone);
        boolean isPhoneMatch = studentInDb.getPhone().equals(emailOrPhone);

        if (!isEmailMatch && !isPhoneMatch) {
            System.out.println("\n Lỗi: Email hoặc Số điện thoại xác thực không khớp với tài khoản!");
            return false;
        }

        // Kiểm tra mật khẩu mới trùng mật khẩu cũ
        if (newPassword.equals(oldPassword)) {
            System.out.println("\n Lỗi: Mật khẩu mới không được trùng với mật khẩu cũ!");
            return false;
        }

        // Gọi DAO lưu mật khẩu mới
        return studentDAO.changePassword(id, newPassword);
    }


    @Override
    public boolean registerCourse(int studentId, int courseId) {
        String currentStatus = enrollmentDAO.checkEnrollmentStatus(studentId, courseId);
        if (currentStatus != null) {
            if ("WAITING".equals(currentStatus)) {
                System.out.println("\n Lỗi: Bạn đã đăng ký khóa học này rồi và đang chờ Admin duyệt!");
                return false;
            } else if ("CONFIRMED".equals(currentStatus)) {
                System.out.println("\n Lỗi: Bạn đã được duyệt tham gia khóa học này rồi!");
                return false;
            }
            // Cho phép đăng ký lại nếu trạng thái cũ là DENIED (Bị từ chối) hoặc CANCEL (Đã hủy)
            else if ("DENIED".equals(currentStatus) || "CANCEL".equals(currentStatus)) {
                return enrollmentDAO.updateStatus(studentId, courseId, "WAITING");
            }
        }
        // Chưa từng đăng ký bao giờ -> Thêm mới bản ghi vào bảng enrollment
        return enrollmentDAO.insertEnrollment(studentId, courseId);
    }

    @Override
    public void displayRegisteredCourses(int studentId, String criteria, String direction) {
        // Lấy danh sách thô (gồm mảng Object liên kết dữ liệu giữa các bảng) từ DAO về
        List<Object[]> data = enrollmentDAO.getRegisteredCoursesWithDetails(studentId, criteria, direction);

        System.out.println("\n" + "=".repeat(85));
        System.out.printf("| %-7s | %-30s | %-20s | %-15s |\n", "ID KH", "Tên Khóa Học", "Ngày Đăng Ký", "Trạng Thái");
        System.out.println("-".repeat(85));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        boolean hasData = false;

        for (Object[] row : data) {
            hasData = true;
            String status = (String) row[3];
            String displayStatus = "";

            switch (status) {
                case "WAITING": displayStatus = "Chờ duyệt"; break;
                case "CONFIRMED": displayStatus = "Đã duyệt"; break;
                case "DENIED": displayStatus = "Bị từ chối"; break;
                case "CANCEL": displayStatus = "Đã hủy"; break;
                default: displayStatus = status;
            }

            System.out.printf("| %-7d | %-30s | %-20s | %-15s |\n",
                    row[0], row[1], sdf.format(row[2]), displayStatus);
        }

        if (!hasData) {
            System.out.println("|                     Bạn chưa đăng ký bất kỳ khóa học nào!                    |");
        }
        System.out.println("=".repeat(85));
    }

    @Override
    public boolean cancelRegistration(int studentId, int courseId) {
        String currentStatus = enrollmentDAO.checkEnrollmentStatus(studentId, courseId);

        // Điều kiện nghiệp vụ: Chỉ được phép hủy khi trạng thái đăng ký là WAITING
        if (currentStatus == null) {
            System.out.println("\n Lỗi: Bạn chưa đăng ký khóa học này!");
            return false;
        }
        if (!"WAITING".equals(currentStatus)) {
            String displayStatus = "CONFIRMED".equals(currentStatus) ? "Đã duyệt" : "Bị từ chối";
            System.out.println("\n Lỗi: Khóa học đã được Admin xử lý (" + displayStatus + "), không thể tự hủy!");
            return false;
        }

        // Cập nhật trạng thái trong database thành 'CANCEL' thông qua DAO
        return enrollmentDAO.updateStatus(studentId, courseId, "CANCEL");
    }
}