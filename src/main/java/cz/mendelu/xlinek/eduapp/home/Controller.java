package cz.mendelu.xlinek.eduapp.home;

import org.springframework.web.bind.annotation.*;


@RestController
public class Controller {

    @GetMapping("/")
    public String welcome() {
        return "Hudcovka Edu API";
    }

}
