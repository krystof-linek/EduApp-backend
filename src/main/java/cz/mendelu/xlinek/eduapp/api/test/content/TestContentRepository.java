package cz.mendelu.xlinek.eduapp.api.test.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestContentRepository extends JpaRepository<TestContent, Long> {
}
