package ra.course.business;

import ra.course.model.Course;
import java.util.List;

public interface ICourseService {
    List<Course> getAllCourses(String sortBy, String direction);
    boolean addCourse(Course course);
    boolean updateCourseField(int courseId, String fieldName, Object value);
    boolean deleteCourse(int courseId);
    List<Course> searchCourseByName(String name);

    void displayStudentsByCourse(int courseId);
    boolean addStudentToCourse(int studentId, int courseId);
    boolean removeStudentFromCourse(int studentId, int courseId);
}