package cz.mendelu.xlinek.eduapp.api.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Test findById(long id);
    Test findByIdAndActiveAndOld(long id, boolean active, boolean old);

    List<Test> findAllByUserEmail(String email);

    List<Test> findAllBySubject_IdSubjectAndActiveAndOld(int id, boolean active, boolean old);
}
