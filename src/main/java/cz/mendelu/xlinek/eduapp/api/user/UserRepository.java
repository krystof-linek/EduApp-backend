package cz.mendelu.xlinek.eduapp.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByClassRoomEqualsAndRoleEquals(String classRoom, String role);
    List<User> findAllByRoleAndValidated(String role, boolean validated);
    List<User> findAllByRole(String role);
    List<User> findAllByRoleAndClassRoomAndGrade(String role, String classRoom, int grade);
    User findUserById(long id);
    User findUserByEmail(String email);
}
