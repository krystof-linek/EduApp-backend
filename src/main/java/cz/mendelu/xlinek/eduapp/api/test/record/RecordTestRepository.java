package cz.mendelu.xlinek.eduapp.api.test.record;

import cz.mendelu.xlinek.eduapp.api.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordTestRepository extends JpaRepository<RecordTest, Long> {
    RecordTest findById(long id);

    List<RecordTest> findAllByTest(Test test);
    List<RecordTest> findAllByTestIdOrderByEndedDesc(long id);
    List<RecordTest> findAllByUserEmailOrderByEndedDesc(String email);
    List<RecordTest> findAllByUserIdOrderByEndedDesc(long id);
}
