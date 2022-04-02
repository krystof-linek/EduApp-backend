package cz.mendelu.xlinek.eduapp.api.course.content;

import cz.mendelu.xlinek.eduapp.api.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findAllByCourse_IdOrderBySequence(int id);
    List<Content> findAllByCourse(Course course);
    Content findById(int id);
}
