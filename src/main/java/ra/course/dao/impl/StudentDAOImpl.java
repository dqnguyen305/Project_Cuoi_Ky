package ra.course.dao.impl;

import ra.course.dao.IStudentDAO;
import ra.course.model.Student;
import ra.course.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements IStudentDAO {

    @Override
    public Student loginStudent(String email, String password) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;
        String sql = "SELECT * FROM student WHERE email = ? AND password = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setPassword(rs.getString("password"));
                student.setCreate_at(rs.getDate("create_at"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(rs, pstmt, con);
        }
        return student;
    }



    @Override
    public List<Student> getAllStudents(String sortBy, String direction) {
        List<Student> list = new ArrayList<>();
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM student";
        if (sortBy != null && !sortBy.isEmpty()) {
            sql += " ORDER BY " + sortBy + " " + (direction.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        }
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setSex(rs.getBoolean("sex"));
                s.setDob(rs.getDate("dob"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setPassword(rs.getString("password"));
                s.setCreate_at(rs.getDate("create_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean addStudent(Student student) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO student (name, sex, dob, email, phone, password, create_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, student.getName());
            pstmt.setBoolean(2, student.isSex());
            pstmt.setDate(3, student.getDob() != null ? new java.sql.Date(student.getDob().getTime()) : null);
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getPassword());
            pstmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public boolean updateStudentField(int studentId, String fieldName, Object value) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "UPDATE student SET " + fieldName + " = ? WHERE id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            if (value instanceof Integer) {
                pstmt.setInt(1, (Integer) value);
            } else if (value instanceof Boolean) {
                pstmt.setBoolean(1, (Boolean) value);
            } else if (value instanceof java.util.Date) {
                pstmt.setDate(1, new java.sql.Date(((java.util.Date) value).getTime()));
            } else {
                pstmt.setString(1, (String) value);
            }
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public boolean deleteStudent(int studentId) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM student WHERE id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi: Học viên này đã đăng ký lớp học, không thể xóa!");
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }

    @Override
    public List<Student> searchStudent(String keyword) {
        List<Student> list = new ArrayList<>();
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM student WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)";
        try {
            int idCheck = -1;
            try {
                idCheck = Integer.parseInt(keyword);
                sql += " OR id = ?";
            } catch (NumberFormatException ignored) {}

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            if (idCheck != -1) {
                pstmt.setInt(3, idCheck);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setSex(rs.getBoolean("sex"));
                s.setDob(rs.getDate("dob"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setPassword(rs.getString("password"));
                s.setCreate_at(rs.getDate("create_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean changePassword(int studentId, String newPassword) {
        Connection con = DBUtil.openConnection();
        PreparedStatement pstmt = null;
        String sql = "UPDATE student SET password = ? WHERE id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConnection(null, pstmt, con);
        }
    }
}