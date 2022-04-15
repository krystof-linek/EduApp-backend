package cz.mendelu.xlinek.eduapp.api.test.record;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test/record")
public class RecordTestController {
    @Autowired
    RecordTestService recordTestService;

    private ResponseStatusException myResponseEx(long errNumber){
        if (errNumber == -400)
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input!");
        if (errNumber == -401)
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions!");
        if (errNumber == -403)
            return new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions!");
        if (errNumber == -404)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "SERVER_ERROR");
    }

    /* ---- GET METHODS ---- */

    @Data
    static class DataResponseOurRecord {
        private RecordTest record;
        private int badQuestions = -1;
        private String successRate = "";
    }

    /**
     * Tento endpoint slouzi k ziskani informaci o konkretnim zaznamu testu
     * @param token autorizacni token
     * @param id_record id zaznamu
     * @return vraci informace o zaznamu
     */
    @GetMapping("/get/my/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponseOurRecord getOwnTestRecordById(Principal token, @PathVariable(value="id") long id_record){

        long status = recordTestService.isAutorOfRecord(token.getName(), id_record);

        if (status != 0){
            throw myResponseEx(status);
        } else {
            DataResponseOurRecord record = recordTestService.getTestRecordById(id_record);

            if (record == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
            else
                return record;
        }

    }
    /**
     * Tento endpoint slouzi pro rodice k ziskani podrobnych informaci ohledne jednoho zaznamu testu sveho ditete.
     * @param token autorizacni token rodice
     * @param id_record zaznam testu ditete
     * @return v pripade uspechu vraci zaznam testu
     */
    @GetMapping("/get/of/my/child/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponseOurRecord getTestRecordOfMyChild(Principal token, @PathVariable(value="id") long id_record){
        long id_child = recordTestService.getIdUserOfTestRecord(id_record);

        if (id_child <= 0)
            throw myResponseEx(id_child);

        long status = recordTestService.isParentOfChild(token.getName(), id_child);

        if (status != 0)
            throw myResponseEx(status);

        return recordTestService.getTestRecordById(id_record);
    }

    /**
     * Endpoint slouzi k ziskani zaznamu testu studenta pro ucitele;
     * @param token autorizacni token
     * @param id_record id zaznam testu
     * @return vraci prislusny zaznam testu;
     */
    @GetMapping("/get/of/my/student/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponseOurRecord getTestRecordOfMyStudent(Principal token, @PathVariable(value="id") long id_record){

        long id_test = recordTestService.getTestIdByRecordId(id_record);

        if (id_test <= 0)
            throw myResponseEx(id_test);

        long status = recordTestService.isCreatorOfTestOrAdmin(token.getName(), id_test);

        if (status != 0)
            throw myResponseEx(status);

        return recordTestService.getTestRecordById(id_record);
    }

    @Data
    static class DataResponseRecord {
        private long id_record = -1;
        private String subject_name = "";
        private String test_name = "";
        private String user_name = "";
        private String user_email = "";
        private LocalDateTime ended;
        private String SuccessRate = "";
    }

    /**
     * Tento endpoint slouží k získání všech testů studenta.
     * @param token autorizační token
     * @return vraci seznam zaznamu o vyplnenych testech
     */
    @GetMapping("/get/all/my/records")
    @ResponseStatus(HttpStatus.OK)
    public List<DataResponseRecord> getAllMyTestRecords(Principal token){

        List<DataResponseRecord> responseRecords =  recordTestService.getAllMyTestRecords(token.getName());

        if (responseRecords == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Records not found");

        return responseRecords;
    }

    /**
     * Tento endpoint slouzi pro rodice k ziskani vsechn zaznamu testu, ktere jeho dite vytvorilo.
     * @param token autorizacni token rodice
     * @param id_child id ditete
     * @return vraci zaznamy vyplneneych testu
     */
    @GetMapping("/get/all/of/my/child/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<DataResponseRecord> getAllTestRecordsOfMyChild(Principal token, @PathVariable(value="id") long id_child){

        long status = recordTestService.isParentOfChild(token.getName(), id_child);

        if (status != 0)
            throw myResponseEx(status);

        return recordTestService.getAllTestRecordsByUserId(id_child);
    }

    @Data
    static class DataResponseRecordTeacher {
        private long id_record = -1;
        private String user_name = "";
        private String user_email = "";
        private String classRoom = "";
        private LocalDateTime ended;
        private String SuccessRate = "";
    }

    /**
     * Enpoint slouzi k nalezeni vsech zaznamu testu, ktere studenti vypracovali na zaklade ID testu.
     * Je overovano, jestli je uzivatel autor testu nebo admin.
     * @param token autorizacni token
     * @param id_test id testu
     * @return vraci seznam vsech vypracovanych zaznamu testu.
     */
    @GetMapping("/get/all/of/my/test/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<DataResponseRecordTeacher> getAllTestRecordsByTestId(Principal token, @PathVariable(value="id") long id_test){

        long status = recordTestService.isCreatorOfTestOrAdmin(token.getName(), id_test);

        if (status != 0)
            throw myResponseEx(status);

        return recordTestService.getAllTestRecordsByTestId(id_test);
    }

    /* ---- POST METHODS ---- */

    @Data
    static class NewRecordTestData {
        private long id_test = -1;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RecordTest createRecordTest(Principal token, @RequestBody NewRecordTestData data){
        long status = recordTestService.createRecordTest(token.getName(), data.id_test);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return recordTestService.getRecordTestById(status);
    }

    @Data
    static class RecordAnswerData {
        private long id_answer = -1;
        private boolean selectedValue = false;
    }
    @Data
    static class UpdateRecordTestData {
        private long id_record = -1;
        List<RecordAnswerData> recordAnswerData = new ArrayList<>();
    }
    /**
     * Tento endpoint slouzi k zpracovani výsledků testu.
     * Do listu se ukládají pouze chybně odpovězené odpovědi.
     * @param token autorizacni token
     * @param data data ke zpracování
     * @return vrací patřičný záznam o výsledku
     */
    @PostMapping("/check/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public RecordTest updateRecordTest(Principal token, @RequestBody UpdateRecordTestData data){
        long status = recordTestService.updateRecordTest(token.getName(), data);

        if (status <= 0) {
            throw myResponseEx(status);
        }
        else
            return recordTestService.getRecordTestById(status);
    }
}
