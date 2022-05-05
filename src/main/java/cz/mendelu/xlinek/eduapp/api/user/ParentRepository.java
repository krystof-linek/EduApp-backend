package cz.mendelu.xlinek.eduapp.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Parent findById(long id);
    Parent findByEmail(String email);
}
