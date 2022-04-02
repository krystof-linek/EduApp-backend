package cz.mendelu.xlinek.eduapp.api.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByTitleEquals(String title);
    Course findById(int id);
    List<Course> findAll();
    List<Course> findAllBySubject_IdSubjectOrderByTitle(int idSubject);
    List<Course> findAllByUserEmail(String email);
}
