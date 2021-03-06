package cz.mendelu.xlinek.eduapp.api.test.question;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RestController
@RequestMapping("test/question")
public class QuestionController {
    @Autowired
    QuestionService questionService;

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

        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "SERVER_ERROR");
    }

    /* ---- POST METHODS ---- */

    @Data
    static class NewQuestionData {
        private String text = "";
        private String picture = "";
        private String equation = "";
        private long id_test = -1;
    }

    /**
     * Endpoint pro prirazeni nove otazky k testu.
     * @param token autorizacni token
     * @param data udaje o otazce
     * @return v pripade uspechu vraci nove vytvorenou otazku
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Question newQuestionOfTest(Principal token, @RequestBody NewQuestionData data){

        long status = questionService.newQuestionOfTest(token, data);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return questionService.getQuestionById(status);
    }

    @Data
    static class UpdateQuestionData {
        private long id_question = -1;
        private String text = "";
        private String picture = "";
        private String equation = "";
    }

    /**
     * Endpoint slouzi k aktualizaci otazky
     * @param token autorizacni token
     * @param data data o otazce
     * @return vraci upravenou otazku
     */
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public Question updateQuestionOfTest(Principal token, @RequestBody UpdateQuestionData  data){

        long status = questionService.updateQuestionOfTest(token, data);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return questionService.getQuestionById(status);
    }

    /* ---- DELETE METHODS ---- */

    @Data
    static class DeleteQuestionData {
        private long id_question = -1;
    }

    /**
     * Endpoint, kter?? slou???? ke smaz??n?? ot??zky
     * @param token autoriza??nn?? token
     * @param id_question id ot??zky
     */
    @DeleteMapping("/delete/by/id/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteQuestionById(Principal token, @PathVariable(value="id") int id_question){
        long status = questionService.deleteQuestionById(token, id_question);

        if (status != 0)
            throw myResponseEx(status);
    }
}
