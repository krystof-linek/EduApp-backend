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

    @Autowired
    private ParentRepository parentRepository;

    /* ---- PRIVATE FUNCTIONS ---- */

    /**
     * Funkce slouží k ověření příslušných oprávnění
     * @param token autorizacni token
     * @return v případě úspechu vrací 0;
     */
    protected long isTeacherOrAdmin(String token){
        TokenInfo tokenInfo = getTokenInfo(token);

        User user = userRepository.findUserByEmail(tokenInfo.getEmail());

        if (user == null)
            return -404L;

        if ( !(user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) )
            return -403L;

        return 0; //ok
    }

    /**
     * Funkce kontroluje, jestli je uzivatel rodic a jestli se parametr id_parent shoduje
     * id kontrolovaneho uzivatele. ==> jestli se jedna o jednu a tu samou osobu
     * @param token autorizacni token
     * @return vraci 0 v pripade uspechu
     */
    protected long isParent(String token, long id_parent){
        TokenInfo tokenInfo = getTokenInfo(token);

        User user = userRepository.findUserByEmail(tokenInfo.getEmail());

        if (user == null)
            return -404L;

        if ( !user.getRole().equals("PARENT") )
            return -403L;

        if (user.getId() != id_parent)
            return -403L;

        return 0; //ok
    }

    /**
     * Funkce slouží k ověření, jestli je účet uživatele ověřen nebo ne.
     * @param email školní email uživatele
     * @return v případě ověření vrací 0
     */
    public long isUserValidated(String email) {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
            return -404L;

        if (!user.isValidated())
            return -409L;
        else
            return 0L;
    }

    private TokenInfo getTokenInfo(String token){
        return new TokenPayload(token).getTokenInfo();
    }

    public User getUserInfoByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    public String getUserRoleByEmail(String email) {
        return userRepository.findUserByEmail(email).getRole();
    }
    protected User getUserInfoById(long id) {
        return userRepository.findUserById(id);
    }

    protected User getUserInfo(String token) {
        return userRepository.findUserByEmail(getTokenInfo(token).getEmail());
    }

    /* ---- SELECT ---- */

    public List<User> findAllByClassAndRole(String classRoom, String role) {
        return userRepository.findAllByClassRoomEqualsAndRoleEquals(classRoom, role);
    }

    protected List<User> findAllNotValidatedParents() {


        return userRepository.findAllByRoleAndValidated("PARENT", false);
    }

    protected List<User> findAllByRole(String role){
        return userRepository.findAllByRole(role);
    }

    /**
     * Funkce vybere vsechny deti uzivatele
     * @param id rodice
     * @return pokud rodic existuje, tak vraci seznam prirazenych deti
     */

    protected List<User> findAllKidsOfParent(long id) {

        Parent parent = parentRepository.findById(id);

        if (parent == null)
            return null;

        return parent.getKids();
    }

    /**
     * Funkce vybere na zaklade parametru seznam studentu.
     * @param data udaje o tride a rocniku
     * @return vraci seznam studentu.
     */
    protected List<User> getStudentsByGradeAndClass(UserController.DataStudentList data) {
        return userRepository.findAllByRoleAndClassRoomAndGrade("STUDENT", data.getClassRoom(), data.getGrade());
    }

    /* ---- INSERT ---- */

    /**
     * Funkce vytvori noveho uzivatele v databazi na zaklade tokenu a dalsich parametru.
     * @param token google id_token
     * @param data informace o uzivateli
     * @return v pripade uspechu vraci id noveho uzivatele, jinak cislo chyby.
     */
    protected long createNewUser(String token, UserController.DataNewUser data) {

        TokenInfo tokenInfo = getTokenInfo(token);

        if(!tokenInfo.isTokenValid())
            return -401L;

        if (getUserInfoByEmail(tokenInfo.getEmail()) != null)
            return -409L;

        User user = new User();

        long id = 0;

        user.setEmail(tokenInfo.getEmail());
        user.setName(tokenInfo.getName());
        user.setSurname(tokenInfo.getSurname());
        user.setPicture(tokenInfo.getPicture());

        String role = data.getRole().toUpperCase();

        if (role.equals("PARENT")){

            Parent parent = new Parent();

            parent.setEmail(user.getEmail());
            parent.setName(user.getName());
            parent.setSurname(user.getSurname());
            parent.setPicture(user.getPicture());
            parent.setRole("PARENT");
            parent.setValidated(false);

            return parentRepository.save(parent).getId();

        } else if (role.equals("ADMIN")) {
            user.setRole(role);
            return userRepository.save(user).getId();
        } else if (role.equals("TEACHER")){
            user.setRole(role);
            return userRepository.save(user).getId();
        } else {
            if (data.getGrade() <= 0 || data.getGrade() > 9)
                return -400L;
            if (data.getClassRoom().equals(""))
                return -400L;

            user.setRole("STUDENT");
            user.setClassRoom(data.getClassRoom().toUpperCase());
            user.setGrade(data.getGrade());
            user.setValidated(true);

            return userRepository.save(user).getId();
        }
    }

    /* ---- UPDATE ---- */

    /**
     * Funkce slouzi k prirazeni ditete k rodici.
     * @param id_kid id studenta
     * @param id_parent id rodice
     * @return v pripade uspechu vraci pozmeneny seznam deti
     */
    protected long addKidToParent(long id_kid, long id_parent) {
        User user = userRepository.findUserById(id_kid);

        if (user == null)
            return -404L;

        if (!user.getRole().equals("STUDENT"))
            return -400L;

        Parent parent = parentRepository.findById(id_parent);

        if (parent == null)
            return -404;

        if (parent.getKids().contains(user))
            return -409;

        parent.getKids().add(user);

        return parentRepository.save(parent).getId();
    }

    /**
     * Funkce slouzi k odebrani ditete od rodice.
     * @param id_kid id studenta
     * @param id_parent id rodice
     * @return v pripade uspechu vraci pozmeneny seznam deti
     */
    protected long removeKidFromParent(long id_kid, long id_parent) {
        User user = userRepository.findUserById(id_kid);

        if (user == null)
            return -404L;

        if (!user.getRole().equals("STUDENT"))
            return -400L;

        Parent parent = parentRepository.findById(id_parent);

        if (parent == null)
            return -404;

        if (!parent.getKids().contains(user))
            return -409;

        parent.getKids().remove(user);

        return parentRepository.save(parent).getId();
    }

    /**
     * Funkce slouzi k nastaveni validace uzivatele
     * @param token autorizacni token
     * @param id id uzivatele
     * @param value hodnota, ktera bude nastavena
     * @return v pripade uspechu vraci id upraveneho zaznamu
     */
    protected long setUserValidation(String token, long id, boolean value) {
        long status = isTeacherOrAdmin(token);

        if (status != 0)
            return status;

        User user = userRepository.findUserById(id);

        if (user == null)
            return -404L;

        user.setValidated(value);

        return userRepository.save(user).getId();

    }
}
