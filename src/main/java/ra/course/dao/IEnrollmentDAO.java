package ra.course.dao;

import ra.course.model.Enrollment;
import java.util.List;

public interface IEnrollmentDAO {
    boolean insertEnrollment(int studentId, int courseId);
    String checkEnrollmentStatus(int studentId, int courseId);
    List<Object[]> getRegisteredCoursesWithDetails(int studentId, String criteria, String direction);
    boolean updateStatus(int studentId, int courseId, String newStatus);
    List<Object[]> getAllRegistrationsForAdmin();
}