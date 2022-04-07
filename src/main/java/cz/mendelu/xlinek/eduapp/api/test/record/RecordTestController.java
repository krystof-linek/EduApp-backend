package cz.mendelu.xlinek.eduapp.api.test.record;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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

    @GetMapping("/get/my/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecordTest getRecordTestById(Principal token, @PathVariable(value="id") long id_record){

        long status = recordTestService.isAutorOfRecord(token.getName(), id_record);

        if (status != 0){
            throw myResponseEx(status);
        } else
            return recordTestService.getRecordTestById(id_record);
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
