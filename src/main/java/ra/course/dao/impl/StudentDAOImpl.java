package ra.course.dao.impl;

import ra.course.dao.IStudentDAO;
import ra.course.model.Student;
import ra.course.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Override public List<Student> getAllStudents(String s, String d) { return null; }
    @Override public boolean addStudent(Student student) { return false; }
    @Override public boolean updateStudentField(int id, String f, Object v) { return false; }
    @Override public boolean deleteStudent(int id) { return false; }
    @Override public List<Student> searchStudent(String k) { return null; }
    @Override public boolean changePassword(int id, String np) { return false; }
}