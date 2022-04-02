package cz.mendelu.xlinek.eduapp.api.test.record;

import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.TestRepository;
import cz.mendelu.xlinek.eduapp.api.test.answer.AnswerRepository;
import cz.mendelu.xlinek.eduapp.api.user.User;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
class RecordTestService {
    @Autowired
    RecordTestRepository recordTestRepository;
    @Autowired
    RecordAnswerRepository recordAnswerRepository;

    @Autowired
    TestRepository testRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    UserRepository userRepository;

    /* ---- PRIVATE ---- */

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
     * Funkce vybere zaznam dle prislusneho ID
     * @param id ID zaznamu testu
     * @return vraci patricny zaznam
     */
    protected RecordTest getRecordTestById(long id) {
        return recordTestRepository.findById(id);
    }

    /* ---- INSERT ---- */

    /**
     * Funkce slouzi k vytvoreni noveho zaznamu o vyplneni testu studentem.
     * Ukladat lze zaznam pouze pro studenty.
     * @param token autorizacni token
     * @param id_test test, pro ktery se vytvari zaznam
     * @return v pripade uspechu vraci ID noveho zaznamu
     */
    protected long createRecordTest(String token, long id_test) {

        User user = userRepository.findUserByEmail(getTokenInfo(token).getEmail());

        if (user == null)
            return -404;

        //ukladat se budou jen zaznamy studentu
        if(!user.getRole().equals("STUDENT"))
            return -403;

        if (id_test <= 0)
            return -400;

        Test test = testRepository.findById(id_test);

        if (test == null)
            return -404;

        RecordTest recordTest = new RecordTest();

        recordTest.setTest(test);
        recordTest.setUser(user);

        return recordTestRepository.save(recordTest).getId();
    }

    /* ---- UPDATE ---- */

    /**
     * Funkce slouzi k ulozeni zaznamu o vyplnenem testu.
     * @param token autorizacni token.
     * @param data data k ulozeni
     * @return vraci chybu nebo ID upraveneho zaznamu
     */

    protected long updateRecordTest(String token, RecordTestController.UpdateRecordTestData data) {
        if (data.getId_record() <= 0)
            return -400;

        User user = userRepository.findUserByEmail(getTokenInfo(token).getEmail());

        if (user == null)
            return -404;

        RecordTest recordTest = recordTestRepository.findById(data.getId_record());

        if (recordTest == null) //zaznam nebyl nalezen
            return -404;

        if (recordTest.getEnded() != null) //zakaz upraveni jiz vyplneneho testu
            return -403;

        if (!recordTest.getUser().getEmail().equals(user.getEmail())) //kontrolujeme, jestli upravu provadi stejny uzivatel
            return -403;

        recordTest.setEnded(LocalDateTime.now()); //datum ukonceni testu

        for (RecordTestController.RecordAnswerData dataAnswer : data.getRecordAnswerData()) { //vlozime zaznamy

            if (dataAnswer.getId_answer() > 0){

                RecordAnswer recordAnswer = new RecordAnswer();

                recordAnswer.setAnswer(answerRepository.getById(dataAnswer.getId_answer()));
                recordAnswer.setSelectedValue(dataAnswer.isSelectedValue());
                recordAnswer.setRecordTest(recordTest);

                recordAnswerRepository.save(recordAnswer);

                recordTest.getRecordAnswers().add(recordAnswer);

            }

        }

        return recordTestRepository.save(recordTest).getId(); //provedeme update

    }
}
