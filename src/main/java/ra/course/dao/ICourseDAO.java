package ra.course.dao;

import ra.course.model.Course;
import java.util.List;

public interface ICourseDAO {
    List<Course> getAllCourses(String sortBy, String direction);
    boolean addCourse(Course course);
    boolean updateCourseField(int courseId, String fieldName, Object value);
    boolean deleteCourse(int courseId);
    List<Course> searchCourseByName(String name);
}