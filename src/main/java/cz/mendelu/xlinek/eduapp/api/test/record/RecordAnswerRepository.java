package cz.mendelu.xlinek.eduapp.api.test.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordAnswerRepository extends JpaRepository<RecordAnswer, Long> {
}
