package ra.course.dao.impl;

import ra.course.dao.IEnrollmentDAO;
import ra.course.utils.DBUtil; // Chuẩn gói utils của dự án

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements IEnrollmentDAO {

    @Override
    public String checkEnrollmentStatus(int studentId, int courseId) {
        String sql = "SELECT status FROM enrollment WHERE student_id = ? AND course_id = ? AND status != 'CANCEL'";
        // Sửa thành DBUtil.openConnection() hoặc DBUtil.getConnection() tùy theo file DBUtil của bạn
        try (Connection conn = DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, studentId);
            pstm.setInt(2, courseId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean insertEnrollment(int studentId, int courseId) {
        String sql = "INSERT INTO enrollment (student_id, course_id, status) VALUES (?, ?, 'WAITING')";
        try (Connection conn = DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, studentId);
            pstm.setInt(2, courseId);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Object[]> getRegisteredCoursesWithDetails(int studentId, String criteria, String direction) {
        List<Object[]> list = new ArrayList<>();
        String orderBy = " ORDER BY e.registered_at DESC";
        if ("name".equals(criteria)) orderBy = " ORDER BY c.name " + direction;
        else if ("register_date".equals(criteria)) orderBy = " ORDER BY e.registered_at " + direction;

        String sql = "SELECT c.id, c.name, e.registered_at, e.status " +
                "FROM enrollment e JOIN course c ON e.course_id = c.id " +
                "WHERE e.student_id = ?" + orderBy;

        try (Connection conn = DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, studentId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("name");
                    row[2] = rs.getTimestamp("registered_at");
                    row[3] = rs.getString("status");
                    list.add(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateStatus(int studentId, int courseId, String newStatus) {
        String sql = "UPDATE enrollment SET status = ? WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, newStatus);
            pstm.setInt(2, studentId);
            pstm.setInt(3, courseId);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Object[]> getAllRegistrationsForAdmin() {
        List<Object[]> list = new ArrayList<>();
        // Câu SQL lấy thông tin ID đăng ký, Tên SV, Tên Khóa học, Ngày đăng ký và Trạng thái
        String sql = "SELECT e.id, s.name AS student_name, c.name AS course_name, e.registered_at, e.status, e.student_id, e.course_id " +
                "FROM enrollment e " +
                "JOIN student s ON e.student_id = s.id " +
                "JOIN course c ON e.course_id = c.id " +
                "WHERE e.status != 'CANCEL' " + // Không hiển thị những lớp học viên đã tự hủy
                "ORDER BY e.registered_at DESC";

        try (Connection conn = DBUtil.openConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("student_name");
                row[2] = rs.getString("course_name");
                row[3] = rs.getTimestamp("registered_at");
                row[4] = rs.getString("status");
                row[5] = rs.getInt("student_id");
                row[6] = rs.getInt("course_id");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}