package cz.mendelu.xlinek.eduapp.api.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findByTitleAndAndGrade(String title, int grade);
    Subject findByIdSubject(long id);
    List<Subject> findAllByGrade(int grade);

}
