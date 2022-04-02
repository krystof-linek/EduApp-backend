package cz.mendelu.xlinek.eduapp.api.test.answer;

import cz.mendelu.xlinek.eduapp.api.course.CourseController;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test/question/answer")
public class AnswerController {
    @Autowired
    AnswerService answerService;

    private ResponseStatusException myResponseEx(long errNumber){
        if (errNumber == -400)
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input!");
        if (errNumber == -401)
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions!");
        if (errNumber == -403)
            return new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions!");

        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "SERVER_ERROR");
    }

    /* ---- GET METHODS ---- */

    /**
     * Endpoint slouzi pro vypis vsech moznych odpovedi pro konkretni otazku
     * @param token autorizacni token
     * @param idQuestion id otazky
     * @return vraci seznam prislusnych zaznamu
     */
    @GetMapping("/all/by/question/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Answer> getAllAnswersByQuestionId(Principal token, @PathVariable(value="id") int idQuestion){

        List<Answer> answers = answerService.getAllAnswersByQuestionId(token.getName() , idQuestion);

        if (answers == null)
            throw myResponseEx(-401);

        return answerService.getAllAnswersByQuestionId(token.getName() , idQuestion);
    }

    /* ---- POST METHODS ---- */

    @Data
    static class ExplainOfAnswer {
        private String text = "";
        private String picture = "";
        private String equation = "";
    }

    @Data
    static class AnswerItem {
        private boolean correct = false;
        private String text = "";
        private String picture = "";
        private String equation = "";
        private boolean hasExplain = false;
        private ExplainOfAnswer explain = new ExplainOfAnswer();
    }

    @Data
    static class NewAnswerList {
        private long id_question = 0;
        List<AnswerItem> answers = new ArrayList<>();
    }

    @PostMapping("/add/list")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Answer> newAnswerListForQuestion(Principal token, @RequestBody NewAnswerList data){
        long status = answerService.newAnswerListForQuestion(token, data);

        if (status <= 0)
            throw myResponseEx(status);

        return answerService.getAllAnswersByQuestionId(token.getName(), status);
    }
}
