package cz.mendelu.xlinek.eduapp.api.user;

import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private TokenInfo getTokenInfo(String token){
        return new TokenPayload(token).getTokenInfo();
    }

    public User getUserInfoByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    public String getUserRoleByEmail(String email) {
        return userRepository.findUserByEmail(email).getRole();
    }
    protected User getUserInfoById(int id) {
        return userRepository.findUserById(id);
    }

    protected User getUserInfo(String token) {
        return userRepository.findUserByEmail(getTokenInfo(token).getEmail());
    }

    /**
     * Funkce vytvori noveho uzivatele v databazi na zaklade tokenu a dalsich parametru.
     * @param token google id_token
     * @param data informace o uzivateli
     * @return v pripade uspechu vraci id noveho uzivatele, jinak cislo chyby.
     */
    protected int createNewUser(String token, UserController.DataNewUser data) {

        TokenInfo tokenInfo = getTokenInfo(token);

        if(!tokenInfo.isTokenValid())
            return -1;

        if (getUserInfoByEmail(tokenInfo.getEmail()) != null)
            return -2;

        User user = new User();

        user.setEmail(tokenInfo.getEmail());
        user.setName(tokenInfo.getName());
        user.setSurname(tokenInfo.getSurname());
        user.setPicture(tokenInfo.getPicture());
        user.setRegistered(new Timestamp(System.currentTimeMillis()).getTime());

        String role = data.getRole().toUpperCase();

        if (role.equals("PARENT")){
            user.setRole(role);
        } else if (role.equals("ADMIN")) {
            user.setRole(role);
        } else if (role.equals("TEACHER")){
            user.setRole(role);
        } else {
            user.setRole("STUDENT");
            user.setClassRoom(data.getClassRoom().toUpperCase());
            user.setValidated(true);
        }

        userRepository.save(user);

        return userRepository.save(user).getId();
    }

    public List<User> findAllByClassAndRole(String classRoom, String role) {
        return userRepository.findAllByClassRoomEqualsAndRoleEquals(classRoom, role);
    }

    protected List<User> findAllNotValidatedParents() {
        return userRepository.findAllByRoleAndValidated("PARENT", false);
    }

}
