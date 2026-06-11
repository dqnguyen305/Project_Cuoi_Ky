package ra.course.dao;

import ra.course.model.Student;
import java.util.List;

public interface IStudentDAO {
    Student loginStudent(String email, String password);
    List<Student> getAllStudents(String sortBy, String direction);
    boolean addStudent(Student student);
    boolean updateStudentField(int studentId, String fieldName, Object value);
    boolean deleteStudent(int studentId);
    List<Student> searchStudent(String keyword);
    boolean changePassword(int studentId, String newPassword);
}