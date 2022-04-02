package cz.mendelu.xlinek.eduapp.api.test.question;


import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.TestRepository;
import cz.mendelu.xlinek.eduapp.api.test.answer.AnswerRepository;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContent;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContentRepository;
import cz.mendelu.xlinek.eduapp.api.test.explanation.ExplanationRepository;
import cz.mendelu.xlinek.eduapp.api.user.User;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Service
public class QuestionService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRepository testRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    TestContentRepository testContentRepository;


    /* ----------- PRIVATE FUNCTIONS ------------ */
    /**
     * Funkce overi, jestli ma uzivatel prislusna opraveni.
     * @param token
     * @return vraci true nebo false
     */
    private long isTeacherOrAdmin(String token) {
        TokenInfo tokenInfo = new TokenPayload(token).getTokenInfo();

        if(!tokenInfo.isTokenValid())
            return -401;

        String role = userRepository.findUserByEmail(tokenInfo.getEmail()).getRole();

        if (!(role.equals("TEACHER") || role.equals("ADMIN")))
            return -403;

        return 0;
    }

    /**
     * Funkce kontroluje, jestli je uzivatel vlastnik testu, s kterym chce manipulovat
     * @param token autorizacni token s daty
     * @param question otazka, s kterou chce uzivatel manipulovat
     * @return v pripade uspechu vraci 0
     */
    private long isOwner(String token, Question question){

        TokenInfo tokenInfo = new TokenPayload(token).getTokenInfo();

        Test test = question.getTest();

        if (!test.getUser().getEmail().equals(tokenInfo.getEmail()))
            return -403;

        return 0;
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
     * Funkce vybere prislusny zaznam o otazce na zaklade ID
     * @param id je ID otazky
     * @return vraci otazku nebo null pokud nebyla nalezen
     */
    public Question getQuestionById(long id) {
        return questionRepository.findById(id);
    }

    /* ---- INSERT ---- */

    /**
     * Funkce vytvori novou otazku a priradi ji k prislusnemu testu.
     * @param token autorizacni token
     * @param data data potrebna k vytvoreni otazky
     * @return vraci cislo chyby nebo ID nove vytvorene otazky
     */
    protected long newQuestionOfTest(Principal token, QuestionController.NewQuestionData data){
        long status = isTeacherOrAdmin(token.getName());

        if (status != 0)
            return status;

        if (data.getId_test() <= 0 ||data.getText().equals(""))
            return -400;

        Test test = testRepository.findById(data.getId_test());

        if(test == null)
            return -400;

        Question question = new Question();

        TestContent testContent = new TestContent(data.getText(), data.getPicture(), data.getEquation());

        testContentRepository.save(testContent);

        if (testContent == null)
            return -400;

        question.setTestContent(testContent);
        question.setSequence(test.getQuestions().size() + 1);
        question.setTest(test);

        status = questionRepository.save(question).getId();

        return status;
    }

    /* ---- UPDATE ---- */

    /**
     * Funkce aktualizuje obsah otazky
     * @param question otazka knkretniho testu
     */
    public Question updateQuestion(Question question){

        return questionRepository.save(question);
    }

    /**
     * Funkce vytvori novou otazku a priradi ji k prislusnemu testu.
     * @param token autorizacni token
     * @param data data potrebna k vytvoreni otazky
     * @return vraci cislo chyby nebo ID nove vytvorene otazky
     */
    protected long updateQuestionOfTest(Principal token, QuestionController.UpdateQuestionData data){
        long status = isTeacherOrAdmin(token.getName());

        if (status != 0)
            return status;

        status = isOwner(token.getName(), questionRepository.findById(data.getId_question()));

        if (status != 0)
            return status;

        Question question = questionRepository.findById(data.getId_question());

        question.getTestContent().setText(data.getText());
        question.getTestContent().setPicture(data.getPicture());
        question.getTestContent().setEquation(data.getEquation());

        questionRepository.save(question);

        return question.getId();
    }

    /* ---- DELETE ---- */

    /**
     * Funkce smaze otazku na zaklade jejího ID
     * @param token pro overeni prislusnych opravneni
     * @param id_question id otazky
     */
    public long deleteQuestionById(Principal token, long id_question) {

        long status = isTeacherOrAdmin(token.getName());

        if (status != 0)
            return status;

        status = isOwner(token.getName(), questionRepository.findById(id_question));

        if (status != 0)
            return status;

        questionRepository.delete(questionRepository.findById(id_question));

        return 0;
    }
}
