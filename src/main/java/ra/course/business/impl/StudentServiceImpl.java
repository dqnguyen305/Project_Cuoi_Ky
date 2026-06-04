package ra.course.business.impl;

import ra.course.business.IStudentService;
import ra.course.dao.IStudentDAO;
import ra.course.dao.impl.StudentDAOImpl;
import ra.course.model.Student;
import java.util.List;

public class StudentServiceImpl implements IStudentService {
    private final IStudentDAO studentDAO = new StudentDAOImpl();

    @Override
    public Student loginStudent(String email, String password) {
        return studentDAO.loginStudent(email, password);
    }

    @Override public List<Student> getAllStudents(String s, String d) { return null; }
    @Override public boolean addStudent(Student student) { return false; }
    @Override public boolean updateStudentField(int id, String f, Object v) { return false; }
    @Override public boolean deleteStudent(int id) { return false; }
    @Override public List<Student> searchStudent(String k) { return null; }
    @Override public boolean changePassword(int id, String np) { return false; }
}