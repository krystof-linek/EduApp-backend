package cz.mendelu.xlinek.eduapp.api.subject;

import cz.mendelu.xlinek.eduapp.api.user.User;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;

    /* ---- PRIVATE FUNCTIONS ---- */

    private TokenInfo getTokenInfo(String token){
        return new TokenPayload(token).getTokenInfo();
    }

    /**
     * Funkce slouží k ověření příslušných oprávnění.
     * @param token autorizacni token
     * @return v pripade, ze uzivatel nema opravneni vyhodi vyjimku.
     */
    protected void isTeacherOrAdmin(String token){
        TokenInfo tokenInfo = getTokenInfo(token);

        User user = userRepository.findUserByEmail(tokenInfo.getEmail());

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");

        if ( !(user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) )
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions'");
    }

    /**
     * Funkce kontroluje, jestli zadany predmet jiz existuje v databazi nebo ne
     * @param title nazev predmetu
     * @param grade rocnik
     * @return vraci true nebo false
     */
    private boolean checkIfExist(String title, int grade){
        List<Subject> subjects = subjectRepository.findAllByGrade(grade);

        if (subjects.size() == 0)
            return false;

        for (Subject subject: subjects) {
            if (subject.getTitle().equals(title))
                return true;
        }

        return false;
    }

    /* ---- SELECT ---- */

    /**
     * Funkce vraci zaznam na zaklade nazvu predmetu a rocniku
     * @param title nazev predmetu
     * @param grade rocnik
     * @return vraci patricny zaznam
     */
    public Subject findSubjectByTitleAndGrade(String title, int grade){
        return subjectRepository.findByTitleAndAndGrade(title, grade);
    }

    /**
     * Funkce vybere vsechny predmety v rocniku
     * @param grade rocnik
     * @return vraci seznam predmetu v rocniku
     */
    public List<Subject> getAllSubjectsByGrade(int grade) {
        return subjectRepository.findAllByGrade(grade);
    }

    /* ---- INSERT ---- */

    /**
     * Funkce slouzi k vytvoreni noveho predmetu
     * Kontroluje se zda již takovy predmet neexistuje
     * @param title nazev predmetu
     * @param grade rocnik
     * @return v pripade platnych vstupních údaju se uloží nový záznam
     */
    public Subject createSubject(String token, String title, int grade){
        isTeacherOrAdmin(token);

        if (title.equals("") || grade <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input");

        title = title.toLowerCase();
        title = title.substring(0, 1).toUpperCase() + title.substring(1);

        if(checkIfExist(title, grade))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject already exist!");

        Subject subject = new Subject();

        subject.setTitle(title);
        subject.setGrade(grade);

        return  subjectRepository.save(subject);
    }
}
