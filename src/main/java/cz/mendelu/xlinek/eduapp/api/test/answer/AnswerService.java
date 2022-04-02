package cz.mendelu.xlinek.eduapp.api.test.answer;

import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContent;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContentRepository;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContentService;
import cz.mendelu.xlinek.eduapp.api.test.explanation.Explanation;
import cz.mendelu.xlinek.eduapp.api.test.explanation.ExplanationRepository;
import cz.mendelu.xlinek.eduapp.api.test.explanation.ExplanationService;
import cz.mendelu.xlinek.eduapp.api.test.question.Question;
import cz.mendelu.xlinek.eduapp.api.test.question.QuestionRepository;
import cz.mendelu.xlinek.eduapp.api.test.question.QuestionService;
import cz.mendelu.xlinek.eduapp.api.user.User;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.api.user.UserService;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class AnswerService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    ExplanationRepository explanationRepository;
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
     * Funkce overi, jestli je uzivatel vlastnik testu
     * @param test
     * @return craci true or false
     */
    private boolean isTestOwner(Test test, String token){
        TokenInfo tokenInfo = new TokenPayload(token).getTokenInfo();

        User user = userRepository.findUserByEmail(tokenInfo.getEmail());

        if(test.getUser() != user)
            return false;

        return true;
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
     * Funkce vrati vsechny odpovedi k prislusne otazce na zaklade ID.
     * @param id = jedna se o ID otazky
     * @return vraci list odpovedi
     */
    protected List<Answer> getAllAnswersByQuestionId(String token, long id){
        long status = isTeacherOrAdmin(token);

        if (status < 0)
            return null;

        return answerRepository.findAllByQuestionId(id);
    }

    /* ---- INSERT ---- */

    /**
     * Funkce priradi odpovedi k dane otazce
     * @param token autorizacni token pro overeni prav
     * @param data data z requestu
     * @return vraci ID upravene otazky nebo chybu
     */
    public long newAnswerListForQuestion(Principal token, AnswerController.NewAnswerList data) {
        if (isTeacherOrAdmin(token.getName()) != 0)
            return -401;

        if (data.getId_question() <= 0)
            return -400;

        Question question = questionRepository.findById(data.getId_question());

        if (question == null)
            return -404;

        if (!isTestOwner(question.getTest(), token.getName()))
            return -403;

        for (AnswerController.AnswerItem answerItem : data.getAnswers()) {

            //kazda odpoved musi mit text jinak ji nema smysl vkladat
            if (!answerItem.getText().equals("")){

                Answer answer = new Answer();

                answer.setTestContent(testContentRepository.save(new TestContent(answerItem.getText(), answerItem.getPicture(), answerItem.getEquation())));
                answer.setTrue(answerItem.isCorrect());

                //pokud ma odpoved vysvetlivku a povinny údaj
                if (answerItem.isHasExplain() && !answerItem.getExplain().getText().equals("")){

                    Explanation explanation = new Explanation();

                    explanation.setTestContent(testContentRepository.save(new TestContent(
                            answerItem.getExplain().getText(),
                            answerItem.getExplain().getPicture(),
                            answerItem.getExplain().getEquation()
                    )));

                    explanationRepository.save(explanation);

                    answer.setExplanation(explanation);

                }
                //priradime k otazce a ulozime
                answer.setQuestion(question);
                question.getAnswers().add(answerRepository.save(answer));
            }
        }

        //ulozime nove otazky k otazce
        return questionRepository.save(question).getId();
    }

    /* ---- UPDATE ---- */
    public List<Answer> updateAnswersOfQuestion(List<Answer> answers) {
        for (Answer answerItem : answers) {
            System.out.println(answerItem);
        }

        return null;
    }
}
