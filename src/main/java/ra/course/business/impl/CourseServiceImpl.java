package ra.course.business.impl;

import ra.course.business.ICourseService;
import ra.course.dao.ICourseDAO;
import ra.course.dao.impl.CourseDAOImpl;
import ra.course.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CourseServiceImpl implements ICourseService {
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    @Override
    public List<Course> getAllCourses(String sortBy, String direction) {
        return courseDAO.getAllCourses(sortBy, direction);
    }

    @Override
    public boolean addCourse(Course course) {
        return courseDAO.addCourse(course);
    }

    @Override
    public boolean updateCourseField(int courseId, String fieldName, Object value) {
        return courseDAO.updateCourseField(courseId, fieldName, value);
    }

    @Override
    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }

    @Override
    public List<Course> searchCourseByName(String name) {
        return courseDAO.searchCourseByName(name);
    }

    @Override
    public void displayStudentsByCourse(int courseId) {
        // Lấy cả WAITING (Chờ duyệt) và CONFIRMED (Đã duyệt)
        String sql = "SELECT s.id, s.name, s.email, e.status " +
                "FROM enrollment e JOIN student s ON e.student_id = s.id " +
                "WHERE e.course_id = ? AND e.status IN ('WAITING', 'CONFIRMED')";

        System.out.println("\n" + "=".repeat(85));
        System.out.printf("| %-7s | %-25s | %-25s | %-15s |\n", "ID SV", "Tên Sinh Viên", "Email", "Trạng Thái");
        System.out.println("-".repeat(85));

        boolean hasData = false;
        try (Connection conn = ra.course.utils.DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, courseId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    hasData = true;
                    String status = rs.getString("status");
                    String displayStatus = "WAITING".equals(status) ? "Chờ phê duyệt" : "Đã duyệt";

                    System.out.printf("| %-7d | %-25s | %-25s | %-15s |\n",
                            rs.getInt("id"), rs.getString("name"), rs.getString("email"), displayStatus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!hasData) {
            System.out.println("|               Hiện tại chưa có học viên nào đăng ký khóa học này!              |");
        }
        System.out.println("=".repeat(85));
    }

    @Override
    public boolean addStudentToCourse(int studentId, int courseId) {
        // SỬA ĐỔI: "Thêm vào khóa học" bản chất chính là Duyệt đơn đăng ký có sẵn
        // Chuyển trạng thái từ 'WAITING' sang 'CONFIRMED'
        String sql = "UPDATE enrollment SET status = 'CONFIRMED' WHERE student_id = ? AND course_id = ? AND status = 'WAITING'";
        try (Connection conn = ra.course.utils.DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, studentId);
            pstm.setInt(2, courseId);

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                return true; // Duyệt thành công
            } else {
                System.out.println("Lỗi: Học viên này không tồn tại trong danh sách chờ duyệt của khóa học!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeStudentFromCourse(int studentId, int courseId) {
        // Thực hiện xóa hoàn toàn bản ghi đăng ký của học viên khỏi khóa học trong bảng trung gian
        String sql = "DELETE FROM enrollment WHERE student_id = ? AND course_id = ?";
        try (Connection conn = ra.course.utils.DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, studentId);
            pstm.setInt(2, courseId);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}