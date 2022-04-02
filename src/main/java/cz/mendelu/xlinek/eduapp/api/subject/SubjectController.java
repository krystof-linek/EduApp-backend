package cz.mendelu.xlinek.eduapp.api.subject;

import cz.mendelu.xlinek.eduapp.api.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    SubjectService subjectService;

    @GetMapping("/all/by/grade/{grade}")
    @ResponseStatus(HttpStatus.OK)
    public List<Subject> getAllSubjectsByGrade(@PathVariable(value="grade") int grade){
        return subjectService.getAllSubjectsByGrade(grade);
    }


}
