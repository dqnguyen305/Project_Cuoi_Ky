package ra.course.business;

import ra.course.model.Student;
import java.util.List;

public interface IStudentService {
    Student loginStudent(String email, String password);
    List<Student> getAllStudents(String sortBy, String direction);
    boolean addStudent(Student student);
    boolean updateStudentField(int studentId, String fieldName, Object value);
    boolean deleteStudent(int studentId);
    List<Student> searchStudent(String keyword);
    boolean changePassword(int id, String oldPassword, String emailOrPhone, String newPassword);
    boolean registerCourse(int studentId, int courseId);
    void displayRegisteredCourses(int studentId, String criteria, String direction);
    boolean cancelRegistration(int studentId, int courseId);
}