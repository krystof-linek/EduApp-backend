package cz.mendelu.xlinek.eduapp.api.test.record;

import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.TestRepository;
import cz.mendelu.xlinek.eduapp.api.test.answer.Answer;
import cz.mendelu.xlinek.eduapp.api.test.answer.AnswerRepository;
import cz.mendelu.xlinek.eduapp.api.test.question.Question;
import cz.mendelu.xlinek.eduapp.api.user.Parent;
import cz.mendelu.xlinek.eduapp.api.user.ParentRepository;
import cz.mendelu.xlinek.eduapp.api.user.User;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
class RecordTestService {
    @Autowired
    RecordTestRepository recordTestRepository;

    @Autowired
    TestRepository testRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ParentRepository parentRepository;

    /* ---- PRIVATE AND PROTECTED FUNCTIONS ---- */

    /**
     * Funkce slouzi k ziskani informaci uvnitr tokenu
     * @param token
     * @return vraci informace o tokenu
     */
    private TokenInfo getTokenInfo(String token){
        return new TokenPayload(token).getTokenInfo();
    }

    /**
     * Funkce kontroluje, jestli je uzivatel rodic ditete dle jeho ID
     * @param token autorizacni token rodice
     * @param id_child id ditete
     * @return vraci 0 pokud je vse v poradku
     */

    protected long isParentOfChild(String token, long id_child){

        TokenInfo tokenInfo = getTokenInfo(token);

        Parent parent = parentRepository.findByEmail(tokenInfo.getEmail());

        if (parent == null)
            return -404L;

        User user = userRepository.findUserById(id_child);

        if (user == null)
            return -404L;

        if (! parent.getKids().contains(user))
            return -403;

        return 0; //ok
    }

    /**
     * Funkce vrati ID studenta, ktery vypracoval prislusny test na zaklade id zaznamu testu
     * @param id_record ID zaznamu testu
     * @return v pripade uspechu vraci id studenta
     */
    protected long getIdUserOfTestRecord(long id_record){
        RecordTest recordTest = recordTestRepository.findById(id_record);

        if (recordTest == null)
            return -404L;

        User user = recordTest.getUser();

        if (user == null)
            return -404L;

        return user.getId();
    }

    /**
     * Funkce zjistuje, jestli je uzivatel admin a nebo autor testu
     * @param token autorizacni token
     * @param id_test id testu
     * @return vraci 0 pokud je vse splneno.
     */
    public long isCreatorOfTestOrAdmin(String token, long id_test) {
        TokenInfo tokenInfo = getTokenInfo(token);

        User user = userRepository.findUserByEmail(tokenInfo.getEmail());

        if (user == null)
            return -404L;

        if (user.getRole().equals("ADMIN"))
            return 0L;

        if (!user.getRole().equals("TEACHER"))
            return -403L;

        if (testRepository.findById(id_test).getUser().equals(user))
            return 0L;
        else
            return -403L;
    }

    /**
     * Funkce vrati ID testu na zaklade ID zaznamu testu
     * @param id_record ID zaznamu testu
     * @return v pripade uspechu vraci ID testu
     */
    protected long getTestIdByRecordId(long id_record) {
        Test test = recordTestRepository.findById(id_record).getTest();

        if (test == null)
            return -404L;
        else
            return test.getId();
    }

    /* ---- SELECT ---- */

    /**
     * Zkontroluje, jestli je uzivatel autor daneho zaznamu dle ID zaznamu testu.
     * @param token autorizacni token
     * @param id_record ID zaznamu testu
     * @return v pripade uspechu vraci 0.
     */
    protected long isAutorOfRecord(String token, long id_record) {
        TokenInfo tokenInfo = getTokenInfo(token);

        if (id_record <= 0 || tokenInfo == null) //chybny vstup
            return -400;

        RecordTest recordTest = recordTestRepository.findById(id_record);

        if (recordTest == null) //zaznam nenalezen
            return -404;

        if (!recordTest.getUser().getEmail().equals(tokenInfo.getEmail())) //uzivatel neni autorem zaznamu
            return -403;

        return 0;
    }

    /**
     * Funkce vybere zaznam dle prislusneho ID
     * @param id ID zaznamu testu
     * @return vraci patricny zaznam
     */
    protected RecordTest getRecordTestById(long id) {
        return recordTestRepository.findById(id);
    }

    /**
     * Funkce vraci udaje o prislusnem zaznamu testu. Tato funkce se vyuziva pro zaznamy, ktere chce primo sam student
     * @param id_record id zaznamu
     * @return vraci patricne udaje
     */
    protected RecordTestController.DataResponseOurRecord getTestRecordById(long id_record) {
        RecordTest recordTest = recordTestRepository.findById(id_record);

        if (recordTest == null)
            return null;

        RecordTestController.DataResponseOurRecord record = new RecordTestController.DataResponseOurRecord();

        record.setRecord(recordTest);

        if (recordTest.getEnded() == null)
            return record;
        else {

            List<Question> badQuestions = new ArrayList<>();

            for (Answer badAnswer : recordTest.getBadAnswers()) {
                if (!badQuestions.contains(badAnswer.getQuestion()))
                    badQuestions.add(badAnswer.getQuestion());
            }

            float all = recordTest.getTest().getQuestions().size();
            float bad = badQuestions.size();

            float successRate = ((all-bad) / all) * 100;

            record.setSuccessRate(Math.round(successRate) + " %");

            record.setBadQuestions(badQuestions.size());

            return record;
        }

    }

    /**
     * Funkce vybere vsechny vypracovane testy studenta
     * @param token autorizacni token
     * @return vraci seznam zaznamu
     */
    protected List<RecordTestController.DataResponseRecord> getAllMyTestRecords(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);

        if (tokenInfo == null)
            return null;

        List<RecordTestController.DataResponseRecord> responseRecords = new ArrayList<>();

        List<RecordTest> records = recordTestRepository.findAllByUserEmailOrderByEndedDesc(tokenInfo.getEmail());

        for (RecordTest record : records) {
            if (record != null){
                RecordTestController.DataResponseRecord responseRecord = new RecordTestController.DataResponseRecord();

                responseRecord.setId_record(record.getId());
                responseRecord.setTest_name(record.getTest().getTitle());
                responseRecord.setSubject_name(record.getTest().getSubject().getTitle());
                responseRecord.setUser_email(record.getUser().getEmail());
                responseRecord.setUser_name(record.getUser().getName() + ' ' + record.getUser().getSurname());
                if (record.getEnded() == null)
                    responseRecords.add(responseRecord);
                else {
                    responseRecord.setEnded(record.getEnded());

                    List<Question> badQuestions = new ArrayList<>();

                    for (Answer badAnswer : record.getBadAnswers()) {
                        if (!badQuestions.contains(badAnswer.getQuestion()))
                            badQuestions.add(badAnswer.getQuestion());
                    }

                    float all = record.getTest().getQuestions().size();
                    float bad = badQuestions.size();

                    float successRate = ((all-bad) / all) * 100;

                    responseRecord.setSuccessRate(Math.round(successRate) + " %");

                    responseRecords.add(responseRecord);
                }
            }
        }

        return responseRecords;
    }

    /**
     * Funkce vybere vsechny vypracovane testy studenta na zaklade jeho ID
     * @param id id studenta
     * @return vraci seznam zaznamu
     */
    protected List<RecordTestController.DataResponseRecord> getAllTestRecordsByUserId(long id) {
        List<RecordTestController.DataResponseRecord> responseRecords = new ArrayList<>();

        List<RecordTest> records = recordTestRepository.findAllByUserIdOrderByEndedDesc(id);

        for (RecordTest record : records) {
            if (record != null){
                RecordTestController.DataResponseRecord responseRecord = new RecordTestController.DataResponseRecord();

                responseRecord.setId_record(record.getId());
                responseRecord.setTest_name(record.getTest().getTitle());
                responseRecord.setSubject_name(record.getTest().getSubject().getTitle());
                responseRecord.setUser_email(record.getUser().getEmail());
                responseRecord.setUser_name(record.getUser().getName() + ' ' + record.getUser().getSurname());
                if (record.getEnded() == null)
                    responseRecords.add(responseRecord);
                else {
                    responseRecord.setEnded(record.getEnded());

                    List<Question> badQuestions = new ArrayList<>();

                    for (Answer badAnswer : record.getBadAnswers()) {
                        if (!badQuestions.contains(badAnswer.getQuestion()))
                            badQuestions.add(badAnswer.getQuestion());
                    }

                    float all = record.getTest().getQuestions().size();
                    float bad = badQuestions.size();

                    float successRate = ((all-bad) / all) * 100;

                    responseRecord.setSuccessRate(Math.round(successRate) + " %");

                    responseRecords.add(responseRecord);
                }
            }
        }

        return responseRecords;
    }

    /**
     * Funkce slouzi k vybrani zaznamu testu studenta pro ucitele.
     * @param id_record id zaznamu testu
     * @return vraci prislusny zaznam
     */
    public RecordTestController.DataResponseRecordTeacher getTestRecordByIdAsTeacher(long id_record) {
        RecordTestController.DataResponseRecordTeacher responseRecord = new RecordTestController.DataResponseRecordTeacher();

        RecordTest recordTest = recordTestRepository.findById(id_record);

        if (recordTest != null){

            responseRecord.setId_record(recordTest.getId());
            responseRecord.setClassRoom(recordTest.getUser().getGrade() + "." + recordTest.getUser().getClassRoom());
            responseRecord.setUser_name(recordTest.getUser().getName() + ' ' + recordTest.getUser().getSurname());
            responseRecord.setUser_email(recordTest.getUser().getEmail());

            if (recordTest.getEnded() == null)
                return responseRecord;
            else {
                responseRecord.setEnded(recordTest.getEnded());

                List<Question> badQuestions = new ArrayList<>();

                for (Answer badAnswer : recordTest.getBadAnswers()) {
                    if (!badQuestions.contains(badAnswer.getQuestion()))
                        badQuestions.add(badAnswer.getQuestion());
                }

                float all = recordTest.getTest().getQuestions().size();
                float bad = badQuestions.size();

                float successRate = ((all-bad) / all) * 100;

                responseRecord.setSuccessRate(Math.round(successRate) + " %");

                return responseRecord;
            }
        } else
            return null;
    }

    /**
     * Funkce nalezne veskere zaznamy testu prislusne k ID testu
     * @param id_test ID testu, pro ktery se budou hledat zaznamy
     * @return vraci seznam informaci o jednotlivych zaznamech.
     */
    public List<RecordTestController.DataResponseRecordTeacher> getAllTestRecordsByTestId(long id_test) {
        List<RecordTestController.DataResponseRecordTeacher> responseRecords = new ArrayList<>();

        List<RecordTest> records = recordTestRepository.findAllByTestIdOrderByEndedDesc(id_test);

        for (RecordTest record : records) {
            if (record != null){
                RecordTestController.DataResponseRecordTeacher responseRecord = new RecordTestController.DataResponseRecordTeacher();

                responseRecord.setId_record(record.getId());
                responseRecord.setClassRoom(record.getUser().getGrade() + "." + record.getUser().getClassRoom());
                responseRecord.setUser_name(record.getUser().getName() + ' ' + record.getUser().getSurname());
                responseRecord.setUser_email(record.getUser().getEmail());

                if (record.getEnded() == null)
                    responseRecords.add(responseRecord);
                else {
                    responseRecord.setEnded(record.getEnded());

                    List<Question> badQuestions = new ArrayList<>();

                    for (Answer badAnswer : record.getBadAnswers()) {
                        if (!badQuestions.contains(badAnswer.getQuestion()))
                            badQuestions.add(badAnswer.getQuestion());
                    }

                    float all = record.getTest().getQuestions().size();
                    float bad = badQuestions.size();

                    float successRate = ((all-bad) / all) * 100;

                    responseRecord.setSuccessRate(Math.round(successRate) + " %");

                    responseRecords.add(responseRecord);
                }
            }
        }

        return responseRecords;
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

                Answer answer = answerRepository.getById(dataAnswer.getId_answer());

                if ( answer != null && (answer.isTrue() != dataAnswer.isSelectedValue()) ){

                    recordTest.getBadAnswers().add(answer);

                }

            }

        }

        return recordTestRepository.save(recordTest).getId(); //provedeme update

    }

}
