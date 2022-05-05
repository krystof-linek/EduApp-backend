package cz.mendelu.xlinek.eduapp.api.user;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Trida obsluhuje funkcionalitu uzivatele
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Funkce slouzi k vyvolani prislusne vyjimky na zaklade chyboveho kodu
     * @param errNumber chybovy kod
     * @return vraci prislusnou vyjimku
     */
    private ResponseStatusException myResponseEx(long errNumber){
        if (errNumber == -400)
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input!");
        if (errNumber == -401)
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions!");
        if (errNumber == -403)
            return new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions!");
        if (errNumber == -404)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        if (errNumber == -409)
            return new ResponseStatusException(HttpStatus.CONFLICT, "Conflict!");

        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "SERVER_ERROR");
    }

    /* ---- GET MAPPING ---- */

    /**
     * Overi existenci emailu v databazi.
     * @param email google email uzivatele
     */
    @GetMapping("/exist/{email}") //vymenil jsem pri prihlaseni za my info
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
     * Tento endpoint slouží k ověření, jestli je účet uživatele ověřen.
     * @param email google email uzivatele
     */
    @GetMapping("/is/validated/by/email/{email}") //zatim jsem nepouzil
    @ResponseStatus(HttpStatus.OK)
    public void isUserValidated(@PathVariable(value="email") String email) {
        long status = userService.isUserValidated(email);

        if (status < 0)
            throw myResponseEx(status);
    }

    /**
     * Tento endpoint slouzi k validaci uctu
     * @param token autorizacni token
     * @param id id uzivatele
     */
    @GetMapping("/validate/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void validateUser(Principal token, @PathVariable(value="id") long id) {
        long status = userService.setUserValidation(token.getName(), id, true);

        if (status < 0)
            throw myResponseEx(status);
    }

    /**
     * Tento endpoint slouzi k deaktivaci uctu
     * @param token autorizacni token
     * @param id id uzivatele
     */
    @GetMapping("/deactivate/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deactivteUser(Principal token, @PathVariable(value="id") long id) {
        long status = userService.setUserValidation(token.getName(), id, false);

        if (status < 0)
            throw myResponseEx(status);
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
     * Endpoint slouzi ke zjisteni vsech rodicovskych uctu
     * @param token autorizacni token
     * @return v pripade uspechu vraci seznam rodicovskych uctu.
     */
    @GetMapping("/parents/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllParentsAccounts(Principal token){
        long status = userService.isTeacherOrAdmin(token.getName());

        if (status != 0)
            throw myResponseEx(status);
        else
            return userService.findAllByRole("PARENT");
    }

    /**
     * Endpoint slouzi k nacteni deti rodice
     * @param token autorizacni token
     * @param id id rodicovskeho uctu
     * @return v pripade uspechu vraci seznam deti
     */
    @GetMapping("/kids/by/parent/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllKidsOfParent(Principal token, @PathVariable(value="id") long id){

        long status = userService.isTeacherOrAdmin(token.getName());

        if (status != 0)
            throw myResponseEx(status);
        else
            return userService.findAllKidsOfParent(id);
    }

    /**
     * Tento endpoint slouzi pro rodice a vraci vsechny jeho deti.
     * @param token autorizacni token
     * @param id id rodice
     * @return vraci seznam jeho deti
     */
    @GetMapping("/get/own/kids/by/parent/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllOwnKids(Principal token, @PathVariable(value="id") long id){

        long status = userService.isParent(token.getName(), id);

        if (status != 0)
            throw myResponseEx(status);
        else
            return userService.findAllKidsOfParent(id);
    }

    /**
     * Endpoint slouzi k prirazeni studenta k rodici
     * @param token autorizacni token
     * @param id_kid id studenta
     * @param id_parent id rodice
     * @return vraci pozmeneny seznam deti rodice
     */
    @GetMapping("/set/kid/{id_kid}/to/parent/{id_parent}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> addKidToParent(Principal token, @PathVariable(value="id_kid") long id_kid, @PathVariable(value="id_parent") long id_parent){

        long status = userService.isTeacherOrAdmin(token.getName());

        if (status != 0)
            throw myResponseEx(status);

        status = userService.addKidToParent(id_kid, id_parent);

        if (status <= 0)
            throw myResponseEx(status);

        return userService.findAllKidsOfParent(status);
    }

    /**
     * Endpoint slouzi k odstraneni studenta od rodice
     * @param token autorizacni token
     * @param id_kid id studenta
     * @param id_parent id rodice
     * @return vraci pozmeneny seznam deti rodice
     */
    @GetMapping("/remove/kid/{id_kid}/from/parent/{id_parent}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> removeKidFromParent(Principal token, @PathVariable(value="id_kid") long id_kid, @PathVariable(value="id_parent") long id_parent){

        long status = userService.isTeacherOrAdmin(token.getName());

        if (status != 0)
            throw myResponseEx(status);

        status = userService.removeKidFromParent(id_kid, id_parent);

        if (status <= 0)
            throw myResponseEx(status);

        return userService.findAllKidsOfParent(status);
    }

    /**
     * Vyhleda vsechny uzivatele prislusne tridy a prislusne role.
     * @param classRoom udava tridu na zakladni skole
     * @return seznam uzivatelu
     */
    @GetMapping("/students/{classRoom}") //chyba s tridou je rozdelena na znak a rocnik
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllByClassAndRole(@PathVariable(value="classRoom") String classRoom) {
        classRoom = classRoom.toUpperCase(Locale.ROOT);
        return userService.findAllByClassAndRole(classRoom, "STUDENT");
    }

    /* ---- POST MAPING ---- */

    @Data
    static class DataNewUser {
        private String classRoom = "";
        private int grade = -1;
        private String role;
    }

    /**
     * Endpoint slouzi k vytvoreni noveho zaznamu uzivatele
     * @param principal zde je ulozen google id_token
     * @param data dalsi potrebna data, ktera nejsou ulozena
     * @return v pripade uspechu vraci noveho uzivatele, jinak patricne chybove hlaseni.
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public User createNewUser(Principal principal, @RequestBody DataNewUser data) {

        String token = principal.getName();

        long status = userService.createNewUser(token, data);

        if (status == -409)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exist!");

        if (status <= 0)
            throw myResponseEx(status);
        else
            return userService.getUserInfoById(status);
    }

    @Data
    static class DataStudentList {
        private String classRoom = "";
        private int grade = -1;
    }

    /**
     * Endpoint slouzi k vybrani studentu na zaklade jejich rocniku a tridy
     * @param principal pro autorizacni token
     * @param data data o tride a rocniku
     * @return vraci prislusny seznam studentu.
     */

    @PostMapping("/get/students/by/grade/and/class")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getStudentsByGradeAndClass(Principal principal, @RequestBody DataStudentList data) {

        long status = userService.isTeacherOrAdmin(principal.getName());

        if (status != 0)
            throw myResponseEx(status);

        return userService.getStudentsByGradeAndClass(data);
    }
}
