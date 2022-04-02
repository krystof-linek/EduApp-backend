package cz.mendelu.xlinek.eduapp.api.subject;

import cz.mendelu.xlinek.eduapp.api.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    public Subject findSubjectByTitleAndGrade(String title, int grade){
        return subjectRepository.findByTitleAndAndGrade(title, grade);
    }

    public Subject createSubject(String title, int grade){
        Subject subject = new Subject();

        subject.setTitle(title.toLowerCase());
        subject.setGrade(grade);

        return  subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjectsByGrade(int grade) {
        return subjectRepository.findAllByGrade(grade);
    }
}
