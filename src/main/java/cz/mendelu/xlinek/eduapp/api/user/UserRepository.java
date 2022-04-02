package cz.mendelu.xlinek.eduapp.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //List<User> findAll();
    List<User> findAllByClassRoomEqualsAndRoleEquals(String classRoom, String role);
    List<User> findAllByRoleAndValidated(String role, boolean validated);
    User findUserById(int id);
    User findUserByEmail(String email);

}
