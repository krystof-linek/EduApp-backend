package cz.mendelu.xlinek.eduapp.api.subject;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    SubjectService subjectService;

    /* ---- GET METHODS ---- */

    @GetMapping("/all/by/grade/{grade}")
    @ResponseStatus(HttpStatus.OK)
    public List<Subject> getAllSubjectsByGrade(@PathVariable(value="grade") int grade){
        return subjectService.getAllSubjectsByGrade(grade);
    }

    /* ---- POST METHODS ---- */

    @Data
    static class DataNewSubject {
        private String title = "";
        private int grade = -1;
    }

    /**
     * Endpoint slouzi k vytvoreni noveho predmetu v databazi
     * @param token autorizacni token
     * @param data data
     * @return v pripade uspechu vraci novy zaznam
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Subject createNewSubject(Principal token, @RequestBody DataNewSubject data) {
       return subjectService.createSubject(token.getName(), data.getTitle(), data.getGrade());
    }


}
