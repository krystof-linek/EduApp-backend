package cz.mendelu.xlinek.eduapp.api.test;

import cz.mendelu.xlinek.eduapp.api.subject.Subject;
import cz.mendelu.xlinek.eduapp.api.subject.SubjectRepository;
import cz.mendelu.xlinek.eduapp.api.test.answer.Answer;
import cz.mendelu.xlinek.eduapp.api.test.answer.AnswerController;
import cz.mendelu.xlinek.eduapp.api.test.question.Question;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    /* ----------- PRIVATE FUNCTIONS ------------ */
    /**
     * Funkce overi, jestli ma uzivatel prislusna opraveni.
     * @param token
     * @return vraci true nebo false
     */
    private long isTeacherOrAdmin(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);

        if(!tokenInfo.isTokenValid())
            return -401;

        String role = userRepository.findUserByEmail(tokenInfo.getEmail()).getRole();

        if (!(role.equals("TEACHER") || role.equals("ADMIN")))
            return -403;

        return 0;
    }

    /**
     * Funkce zkontroluje, jestli ma uzivatel prislusna opravenni či nikoliv.
     * @param token autorizacni token
     * @param test test pro overeni
     * @return vraci 0 v pripade uspechu.
     */
    private long hasPermissions(String token, Test test){
        long status = isTeacherOrAdmin(token);

        if (status != 0)
            return status;

        if (test == null)
            return -404;

        TokenInfo tokenInfo = getTokenInfo(token);

        if (test.getUser().getEmail().equals(tokenInfo.getEmail()))
            return 0;

        return -403;
    }

    /**
     * Funkce slouzi k ziskani informaci uvnitr tokenu
     * @param token
     * @return vraci informace o tokenu
     */
    private TokenInfo getTokenInfo(String token){
        return new TokenPayload(token).getTokenInfo();
    }

    /* ---- SELECT ---- */

    /**
     * Funkce vybere prislusny zaznam o testu na zaklade ID
     * @param id id testu
     * @return vraci prislusny test nebo null pokud nebyl nalezen
     */
    public Test getTestById(long id) {
        return testRepository.findById(id);
    }

    /**
     * Funkce vrati vsechny testy, u kterych je uzivatel autorem
     * @param token = autorizacni token
     * @return vraci list zaznamu
     */
    protected List<Test> getAllTestsOfUser(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);

        List<Test> tests = testRepository.findAllByUserEmail(tokenInfo.getEmail());

        for (Test test : tests) {
            test.getQuestions().clear();
            test.setUser(null);
        }

        return tests;
    }

    /**
     * Funkce vrati zaznam testu na zaklade ID a dalsich parametru.
     * @param id = ID testu
     * @param active = jestli je test aktivni nebo ne
     * @param old = udava, jestli se test jeste pouziva nebo ne
     * @return vraci nalezeny zaznam nebo null
     */
    protected Test getTestByIdAndActiveAndOld(long id, boolean active, boolean old) {
        Test test = testRepository.findByIdAndActiveAndOld(id, active, old);

        return test;
    }
    /**
     * Funkce vrati zaznam testu v bezpecne podobe na zaklade ID a dalsich parametru.
     * @param id = ID testu
     * @param active = jestli je test aktivni nebo ne
     * @param old = udava, jestli se test jeste pouziva nebo ne
     * @return vraci nalezeny zaznam nebo null
     */
    protected Test getActiveSafeTestById(long id, boolean active, boolean old) {
        Test test = testRepository.findByIdAndActiveAndOld(id, active, old);

        if (test == null)
            return null;

        //odstranime pripadne otazky, ktere nemaji zadne odpovedi
        for (int i = 0; i < test.getQuestions().size(); i++){
            if (test.getQuestions().get(i).getAnswers().size() == 0)
                test.getQuestions().remove(i); // ==> otazka, ktera nema zadne odpovedi
        }

        //po vyrazeni nekterych otazek uz test zadne neobsahuje ==> nema smysl vracet
        if (test.getQuestions().size() == 0)
            return null;
        //nastavime vsechny odpovedi na false a zbavime se pripadnych vysvetlivek (nejsou potreba)
        for (Question question : test.getQuestions()) {
            for (Answer answer : question.getAnswers()){
                answer.setExplanation(null);
                answer.setTrue(false);
            }
        }
        return test;
    }

    /* ---- INSERT ---- */

    /**
     * Funkce slouzi k vytvoreni zaznamu o novem kurzu.
     * @param token autorizacni token
     * @param data data z formulare
     * @return vraci chybovy status nebo id nove vytvoreneho zaznamu
     */
    protected long createNewTest(Principal token, TestController.newTestData data){
        long status = isTeacherOrAdmin(token.getName());

        if (status != 0)
            return status;

        if (data.getTitle().equals("") || data.getSubject_title().equals("") || data.getSubject_grade() <= 0)
            return -400;

        Subject subject = subjectRepository.findByTitleAndAndGrade(data.getSubject_title(), data.getSubject_grade());

        if(subject == null)
            return -400;

        TokenInfo tokenInfo = getTokenInfo(token.getName());

        Test test = new Test();

        test.setTitle(data.getTitle());
        test.setSubject(subject);
        //author
        test.setUser(userRepository.findUserByEmail(tokenInfo.getEmail()));

        return testRepository.save(test).getId();
    }

    /**
     * Funkce prida novou otazku k testu
     * @param test = test, ke ktremu bude otazka prirazena
     * @param question = otazka
     */
    public void addQuestionToTest(Test test, Question question){
        test.getQuestions().add(question);

        testRepository.save(test);
    }

    /* ---- UPDATE ---- */

    /**
     * Funkce nalezne test dle ID, overi prislusna opravneni a provede zmenu.
     * @param token autorizacni token
     * @param id tesu
     * @return v pripade uspechu vraci ID upraveneho zaznamu.
     */
    protected long changeTestActiveById(Principal token, long id, boolean value) {
        Test test = testRepository.findById(id);

        long status = hasPermissions(token.getName(), test);

        if (status != 0)
            return status;

        test.setActive(value);

        return testRepository.save(test).getId();
    }

    /**
     * Funkce slouzi k upraveni zakladnich informaci testu
     * @param token autorizacni token
     * @param data data k ulozeni
     * @return vraci chybu nebo ID testu
     */
    protected long updateTestById(Principal token, TestController.updateTestData data) {
        Test test = testRepository.findById(data.getId_test());

        long status = hasPermissions(token.getName(), test);

        if (status != 0)
            return status;

        if (data.getId_subject() <= 0 || data.getTitle().equals(""))
            return -400;

        Subject subject = subjectRepository.findByIdSubject(data.getId_subject());

        if (subject == null)
            return -404;

        test.setActive(data.isActive());
        test.setSubject(subject);
        test.setTitle(data.getTitle());
        test.setOpen(data.isOpen());

        return testRepository.save(test).getId();
    }
}
