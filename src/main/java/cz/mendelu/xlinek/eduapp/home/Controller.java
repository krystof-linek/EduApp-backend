package cz.mendelu.xlinek.eduapp.home;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class Controller {

    @GetMapping("/")
    public String helloWorld() {
        return "Hello Wrold";
    }

    @GetMapping("/help")
    public String restricted(HttpServletRequest request){

        return request.getUserPrincipal().getName();

        //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor Not Found");
        //return "You are authenticated";
    }
    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
        // token can be revoked here if needed
        new SecurityContextLogoutHandler().logout(request, null, null);
        try {
            //sending back to client app
            response.sendRedirect(request.getHeader("referer"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
