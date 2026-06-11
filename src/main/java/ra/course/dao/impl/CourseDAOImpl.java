package ra.course.dao.impl;

import ra.course.dao.ICourseDAO;
import ra.course.model.Course;
import ra.course.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements ICourseDAO {

    @Override
    public List<Course> getAllCourses(String sortBy, String direction) {
        List<Course> list = new ArrayList<>();
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM course";
        if (sortBy != null && !sortBy.isEmpty()) {
            sql += " ORDER BY " + sortBy + " " + (direction.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        }

        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setDuration(rs.getInt("duration"));
                c.setInstructor(rs.getString("instructor"));
                c.setCreate_at(rs.getDate("create_at"));
                list.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean addCourse(Course course) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO course (name, duration, instructor, create_at) VALUES (?, ?, ?, ?)";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getDuration());
            pstmt.setString(3, course.getInstructor());
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public boolean updateCourseField(int courseId, String fieldName, Object value) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "UPDATE course SET " + fieldName + " = ? WHERE id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            if (value instanceof Integer) {
                pstmt.setInt(1, (Integer) value);
            } else {
                pstmt.setString(1, (String) value);
            }
            pstmt.setInt(2, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public boolean deleteCourse(int courseId) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM course WHERE id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi: Khóa học này đã có học viên đăng ký, không thể xóa!");
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public List<Course> searchCourseByName(String name) {
        List<Course> list = new ArrayList<>();
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM course WHERE LOWER(name) LIKE LOWER(?)";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setDuration(rs.getInt("duration"));
                c.setInstructor(rs.getString("instructor"));
                c.setCreate_at(rs.getDate("create_at"));
                list.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(rs, pstmt, con);
        }
        return list;
    }
}