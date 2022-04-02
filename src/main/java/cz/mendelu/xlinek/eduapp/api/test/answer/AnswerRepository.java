package cz.mendelu.xlinek.eduapp.api.test.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository  extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestionId(long id);
}
