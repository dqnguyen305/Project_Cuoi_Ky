package ra.course.presentation;

import ra.course.business.ICourseService;
import ra.course.business.IStudentService;
import ra.course.business.impl.CourseServiceImpl;
import ra.course.business.impl.StudentServiceImpl;
import ra.course.model.Course;
import ra.course.model.Student;

import java.util.List;
import java.util.Scanner;

public class StudentView {
    private final Scanner scanner;
    private final IStudentService studentService = new StudentServiceImpl();
    private final ICourseService courseService = new CourseServiceImpl();
    private Student currentStudent;

    public StudentView(Scanner scanner, Student currentStudent) {
        this.scanner = scanner;
        this.currentStudent = currentStudent;
    }

    public void showStudentMenu() {
        AdminView adminViewHelper = new AdminView(scanner);

        while (true) {
            System.out.println("\n========= MENU CHÍNH - HỌC VIÊN =========");
            System.out.println("1. Xem danh sách khóa học & Tìm kiếm");
            System.out.println("2. Đăng ký khóa học");
            System.out.println("3. Xem khóa học đã đăng ký & Sắp xếp");
            System.out.println("4. Hủy đăng ký khóa học (Chờ xác nhận)");
            System.out.println("5. Đổi mật khẩu tài khoản");
            System.out.println("6. Đăng xuất");
            System.out.println("=========================================");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Lỗi: Lựa chọn không được để trống!");
                continue;
            }

            switch (choice) {
                case "1":
                    handleViewAndSearchCourses(adminViewHelper);
                    break;
                case "2":
                    handleRegisterCourse();
                    break;
                case "3":
                    handleViewRegisteredCourses();
                    break;
                case "4":
                    handleCancelRegistration();
                    break;
                case "5":
                    handleChangePassword();
                    break;
                case "6":
                    System.out.println("Học viên " + currentStudent.getName() + " đã đăng xuất.");
                    return;
                default:
                    System.out.println("Lựa chọn sai, vui lòng nhập lại từ 1 đến 6.");
            }
        }
    }

    // xem danh sách
    private void handleViewAndSearchCourses(AdminView adminViewHelper) {
        while (true) {
            System.out.println("\n--- XEM & TÌM KIẾM KHÓA HỌC ---");
            System.out.println("1. Hiển thị toàn bộ khóa học");
            System.out.println("2. Tìm kiếm khóa học theo tên");
            System.out.println("3. Quay lại menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) { System.out.println("Lỗi: Lựa chọn không được để trống!"); continue; }

            if (choice.equals("1")) {
                adminViewHelper.displayCourses(courseService.getAllCourses(null, "asc"));
            } else if (choice.equals("2")) {
                String keyword = "";
                while (true) {
                    System.out.print("Nhập tên khóa học cần tìm: ");
                    keyword = scanner.nextLine().trim();
                    if (keyword.isEmpty()) { System.out.println("Lỗi: Từ khóa không được để trống!"); continue; }
                    break;
                }
                adminViewHelper.displayCourses(courseService.searchCourseByName(keyword));
            } else if (choice.equals("3")) {
                return;
            } else {
                System.out.println("Lỗi: Vui lòng nhập từ 1 đến 3!");
            }
        }
    }

    //đăng ký
    private void handleRegisterCourse() {
        System.out.println("\n--- ĐĂNG KÝ KHÓA HỌC MỚI ---");
        int courseId;
        while (true) {
            System.out.print("Nhập ID khóa học muốn đăng ký: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID không được để trống!"); continue; }
            try {
                courseId = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID khóa học phải là số nguyên!"); }
        }

        // Kiểm tra xem khóa học có tồn tại trên hệ thống không
        final int finalCourseId = courseId;
        boolean courseExists = courseService.getAllCourses(null, "asc").stream().anyMatch(c -> c.getId() == finalCourseId);

        if (!courseExists) {
            System.out.println("Lỗi: Không tìm thấy khóa học nào có ID = " + courseId);
            return;
        }

        // ĐÃ SỬA: Gọi xuống DB qua Service, nếu thành công mới báo chữ Đăng ký thành công
        if (studentService.registerCourse(currentStudent.getId(), courseId)) {
            System.out.println(" Đăng ký thành công khóa học ID [" + courseId + "]! Vui lòng chờ Admin phê duyệt.");
        }
    }

    // xem và sắp xếp
    private void handleViewRegisteredCourses() {
        while (true) {
            System.out.println("\n--- DANH SÁCH KHÓA HỌC ĐÃ ĐĂNG KÝ ---");
            System.out.println("1. Xem danh sách mặc định");
            System.out.println("2. Sắp xếp theo Tên khóa học");
            System.out.println("3. Sắp xếp theo Ngày đăng ký");
            System.out.println("4. Quay lại menu chính");
            System.out.print("Nhập lựa chọn (1-4): ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) { System.out.println("Lỗi: Không được để trống!"); continue; }
            if (choice.equals("4")) return;
            if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                System.out.println("Lỗi: Vui lòng nhập đúng từ 1 đến 4!");
                continue;
            }

            String direction = "asc";
            if (!choice.equals("1")) {
                while (true) {
                    System.out.print("Chọn hướng sắp xếp (1: Tăng dần | 2: Giảm dần): ");
                    String dirInput = scanner.nextLine().trim();
                    if (dirInput.isEmpty()) { System.out.println("Lỗi: Không được để trống!"); continue; }
                    if (dirInput.equals("1")) { direction = "asc"; break; }
                    if (dirInput.equals("2")) { direction = "desc"; break; }
                    System.out.println("Lỗi: Chỉ chọn số 1 hoặc 2!");
                }
            }

            String criteria = choice.equals("2") ? "name" : (choice.equals("3") ? "register_date" : "default");

            // ĐÃ SỬA: Xóa bỏ toàn bộ phần in bảng cứng nhắc cũ.
            // Gọi hàm trong Service để tự động query DB và render ra bảng chuẩn có dữ liệu thực tế.
            studentService.displayRegisteredCourses(currentStudent.getId(), criteria, direction);
            break;
        }
    }

    // Hủy đăng ký
    private void handleCancelRegistration() {
        System.out.println("\n--- HỦY ĐĂNG KÝ KHÓA HỌC ---");
        int courseId;
        while (true) {
            System.out.print("Nhập ID khóa học bạn muốn hủy đăng ký: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID không được để trống!"); continue; }
            try {
                courseId = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID phải là số nguyên!"); }
        }

        System.out.print("Bạn có chắc chắn muốn hủy đăng ký khóa học này không? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            // ĐÃ SỬA: Gọi xuống service để check điều kiện status = 'WAITING' và thực thi query đổi trạng thái
            if (studentService.cancelRegistration(currentStudent.getId(), courseId)) {
                System.out.println(" Xử lý hủy đăng ký khóa học thành công!");
            }
        } else {
            System.out.println("Đã hủy bỏ thao tác.");
        }
    }

    private void handleChangePassword() {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");

        if (currentStudent == null) {
            System.out.println("Lỗi: Bạn cần đăng nhập để thực hiện chức năng này!");
            return;
        }

        String oldPassword;
        while (true) {
            System.out.print("Nhập mật khẩu cũ hiện tại: ");
            oldPassword = scanner.nextLine().trim();
            if (oldPassword.isEmpty()) { System.out.println("Lỗi: Mật khẩu cũ không được để trống!"); continue; }
            break;
        }

        String emailOrPhone;
        while (true) {
            System.out.print("Nhập Email hoặc Số điện thoại xác thực: ");
            emailOrPhone = scanner.nextLine().trim();
            if (emailOrPhone.isEmpty()) { System.out.println("Lỗi: Thông tin xác thực không được để trống!"); continue; }
            break;
        }

        String newPassword;
        while (true) {
            System.out.print("Nhập mật khẩu mới muốn thay đổi: ");
            newPassword = scanner.nextLine().trim();
            if (newPassword.isEmpty()) { System.out.println("Lỗi: Mật khẩu mới không được để trống!"); continue; }
            if (newPassword.length() < 6) {
                System.out.println("Lỗi: Mật khẩu mới phải có ít nhất 6 ký tự!");
                continue;
            }
            break;
        }


        if (studentService.changePassword(currentStudent.getId(), oldPassword, emailOrPhone, newPassword)) {
            System.out.println(" Đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho lần đăng nhập sau.");
            currentStudent.setPassword(newPassword); // Cập nhật lại session hiện tại
        } else {
            System.out.println(" Đổi mật khẩu thất bại. Vui lòng kiểm tra lại thông tin!");
        }
    }
}