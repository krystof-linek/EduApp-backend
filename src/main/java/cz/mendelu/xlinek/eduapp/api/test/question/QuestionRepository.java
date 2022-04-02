package cz.mendelu.xlinek.eduapp.api.test.question;

import cz.mendelu.xlinek.eduapp.api.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findById(long id);
}
