package cz.mendelu.xlinek.eduapp.api.test.explanation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExplanationRepository extends JpaRepository<Explanation, Long> {
}
