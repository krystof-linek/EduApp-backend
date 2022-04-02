package cz.mendelu.xlinek.eduapp.api.user;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /* ---- GET MAPPING ---- */

    /**
     * Overi existenci emailu v databazi.
     * @param email google email uzivatele
     */
    @GetMapping("/exist/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void getUserInfo(@PathVariable(value="email") String email) {
        if(userService.getUserInfoByEmail(email) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }
    /**
     * Vrati zaznam uzivatele na zaklade emailu
     * @param email google email uzivatele
     */
    @GetMapping("/by/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserByEmail(@PathVariable(value="email") String email) {
        User user = userService.getUserInfoByEmail(email);
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        return user;
    }

    /**
     * Funkce slouzi k ziskani udaju uzivatele
     * @param token autorizacni token
     * @return vraci patricny zaznam
     */
    @GetMapping("/my/info")
    @ResponseStatus(HttpStatus.OK)
    public User getUserInfo(Principal token) {
        User user = userService.getUserInfo(token.getName());

        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        return user;
    }

    @GetMapping("/parents/not/validated")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllNotValidatedParents() {
        return userService.findAllNotValidatedParents();
    }
    /**
     * Vyhleda vsechny uzivatele prislusne tridy a prislusne role.
     * @param classRoom udava tridu na zakladni skole
     * @return seznam uzivatelu
     */
    @GetMapping("/students/{classRoom}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllByClassAndRole(@PathVariable(value="classRoom") String classRoom) {
        classRoom = classRoom.toUpperCase(Locale.ROOT);
        return userService.findAllByClassAndRole(classRoom, "STUDENT");
    }
    @Data
    static class DataNewUser {
        private String classRoom;
        private String role;
    }
    /**
     *
     * @param principal zde je ulozen google id_token
     * @param data dalsi potrebna data, ktera nejsou ulozena
     * @return v pripade uspechu vraci noveho uzivatele, jinak patricne chybove hlaseni.
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public User createNewUser(Principal principal, @RequestBody DataNewUser data) {

        String token = principal.getName();

        int status = userService.createNewUser(token, data);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token is not valid!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exist!");
        else
            return userService.getUserInfoById(status);
    }
}
