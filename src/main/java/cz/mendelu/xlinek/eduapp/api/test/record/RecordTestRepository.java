package cz.mendelu.xlinek.eduapp.api.test.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordTestRepository extends JpaRepository<RecordTest, Long> {
    RecordTest findById(long id);
}
