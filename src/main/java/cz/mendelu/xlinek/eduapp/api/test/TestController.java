package cz.mendelu.xlinek.eduapp.api.test;

import cz.mendelu.xlinek.eduapp.api.course.CourseController;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    TestService testService;

    private ResponseStatusException myResponseEx(long errNumber){
        if (errNumber == -400)
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input!");
        if (errNumber == -401)
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions!");
        if (errNumber == -403)
            return new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions!");
        if (errNumber == -404)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "SERVER_ERROR");
    }

    /* ---- GET METHODS ---- */

    /**
     * Endpoint slouzi k ziskani testu na zaklade jeho id
     * @param id id testu
     * @return vraci prislusny zaznam
     */
    @GetMapping("/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Test getTestById(@PathVariable(value="id") long id){
        Test test = testService.getTestById(id);

        if (test == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found!");
        else
            return test;
    }

    /**
     * Endpoint slouzi k nacteni aktivniho testu
     * @param id = ID testu
     * @return v pripade uspechu vraci úatricny zaznam
     */
    @GetMapping("/active/load/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Test getActiveTestById(@PathVariable(value="id") long id){
        Test test = testService.getTestByIdAndActiveAndOld(id, true, false);

        if (test == null)
            throw myResponseEx(-404);
        else
            return test;
    }
    /**
     * Endpoint slouzi k nacteni aktivniho testu
     * @param id = ID testu
     * @return v pripade uspechu vraci úatricny zaznam
     */
    @GetMapping("/get/active/safe/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Test getActiveSafeTestById(@PathVariable(value="id") long id){
        Test test = testService.getActiveSafeTestById(id, true, false);

        if (test == null)
            throw myResponseEx(-404);
        else
            return test;
    }

    @GetMapping("/get/all/active/by/subject/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Test> getAllActiveTestsBySubjectId(@PathVariable(value="id") int id){
        return testService.getAllActiveTestsBySubjectId(id);
    }

    /**
     * Endpoint slouzi k deaktivaci testu ==> studenti ho nebudou moci vyplnovat
     * @param token = autorizacni token
     * @param id = id testu
     * @return v pripade uspechu vraci upraveny zaznam
     */
    @GetMapping("/deactivate/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Test deactivateTestById(Principal token, @PathVariable(value="id") long id){
        long status = testService.changeTestActiveById(token, id, false);

        if (status <= 0)
            throw myResponseEx(status);
        else
            return testService.getTestById(status);
    }

    /**
     * Endpoint slouzi k aktivaci testu ==> studenti ho budou moci vyplnovat
     * @param token = autorizacni token
     * @param id = id testu
     * @return v pripade uspechu vraci upraveny zaznam
     */
    @GetMapping("/activate/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Test activateTestById(Principal token, @PathVariable(value="id") long id){
        long status = testService.changeTestActiveById(token, id, true);

        if (status <= 0)
            throw myResponseEx(status);
        else
            return testService.getTestById(status);
    }

    /**
     * Endpoint vrati vsechny testy, u kterych je uzivatel autorem.
     * @param token = autorizacni token
     * @return vraci list zaznamu
     */
    @GetMapping("/all/of/user")
    @ResponseStatus(HttpStatus.OK)
    public List<Test> getAllTestsOfUser(Principal token){
        return testService.getAllTestsOfUser(token.getName());
    }

    /* ---- POST METHODS ---- */

    @Data
    static class newTestData {
        private String title = "";
        private String subject_title = "";
        private int subject_grade = 0;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Test createNewTest(Principal token, @RequestBody TestController.newTestData data){
        long status = testService.createNewTest(token, data);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return testService.getTestById(status);
    }

    @Data
    static class updateTestData {
        private String title = "";
        private int id_subject = -1; //upravit na long
        private long id_test = -1;
        private boolean active = false;
        private boolean open = false;
    }

    @PostMapping("/update/info")
    @ResponseStatus(HttpStatus.CREATED)
    public Test updateTestById(Principal token, @RequestBody TestController.updateTestData data){

        long status = testService.updateTestById(token, data);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return testService.getTestById(status);
    }
}
