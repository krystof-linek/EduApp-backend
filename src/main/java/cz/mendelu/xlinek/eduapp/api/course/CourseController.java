package cz.mendelu.xlinek.eduapp.api.course;

import cz.mendelu.xlinek.eduapp.api.course.content.Content;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseService courseService;



    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Course> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping("/all/by/subject/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Course> getAllCoursesBySubjectId(@PathVariable(value="id") long idSubject){
        return courseService.getAllCoursesBySubjectId(idSubject);
    }

    /**
     * Endpoint slouzi k ziskani vsech kurzu prislusneho autora.
     * @param principal slouzi k ziskani tokenu
     * @return pokud uzivatel nema prislusne opravneni, tak vrati null jinak prislusne zaznamy
     */
    @GetMapping("/all/from/author")
    @ResponseStatus(HttpStatus.OK)
    public List<Course> getAllCoursesFromAuthor(Principal principal){
        String token = principal.getName();

        List<Course> courses = courseService.getAllCoursesFromAuthor(token);

        if (courses == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission!");

        return courses;
    }

    @GetMapping("/content/by/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Content> getCourseContentById(@PathVariable(value="id") long idCourse){
        return courseService.getCourseContentById(idCourse);
    }

    /* -------- POST ENDPOINT --------- */

    @Data
    static class DataNewCourse {
        private String course_title;
        private String subject_title;
        private int grade;
    }
    /**
     * Endpoint k vytvoreni noveho kurzu;
     * @param principal obsahuje autorizacni token
     * @param data data v prislusnem formatu.
     * @return vraci data nove vytvoreneho kurzu
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Course newCourse(Principal principal, @RequestBody DataNewCourse data) {

        String token = principal.getName();

        long status = courseService.newCourse(token, data);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course already exist!");
        else
            return courseService.getCourseById(status);
    }

    @Data
    static class VideoData {
        private String title = "";
        private String description = "";
        private String author = "";
        private String link = "";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu video.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/video")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentVideo(Principal token, @RequestBody VideoData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentVideo(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }

    @Data
    static class PictureData {
        private String title = "";
        private String description = "";
        private String author = "";
        private String alt = "";
        private String link = "";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu obrazek.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/picture")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentPicture(Principal token, @RequestBody PictureData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentPicture(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }

    @Data
    static class ParagraphData {
        private String title = "";
        private int title_size = 1;
        private String description = "";
        private String text = "";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu odstavec.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/paragraph")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentParagraph(Principal token, @RequestBody ParagraphData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentParagraph(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }

    @Data
    static class TitleData {
        private String title = "";
        private int title_size = 1;
        private String icon = "";
        private String icon_color = "";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu nadpis.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/title")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentTitle(Principal token, @RequestBody TitleData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentTitle(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }

    @Data
    static class ListData {
        private String title = "";
        private String description = "";
        private boolean numbered = false;
        private List<String> items;
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu list.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/list")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentList(Principal token, @RequestBody ListData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentList(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }

    @Data
    static class LinkData {
        private String title = "";
        private String description = "";
        private String link = "";
        private String btn_color = "blue";
        private String btn_title = "navštívit";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu link.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/link")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentLink(Principal token, @RequestBody LinkData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentLink(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }
    @Data
    static class NoticeData {
        private String title = "";
        private String description = "";
        private String color = "blue";
        private String icon = "";
    }
    /**
     * Endpoint k vytvoreni noveho obsahu typu upozornění.
     * @param token autorizacni token
     * @param data data v prislusnem formatu
     * @param idCourse ID kurzu, kteremu bude obsah prirazen
     * @return vraci nove vytvoreny zaznam nebo chybu
     */
    @PostMapping("/{id}/new/notice")
    @ResponseStatus(HttpStatus.CREATED)
    public Content newContentNotice(Principal token, @RequestBody NoticeData data, @PathVariable(value="id") long idCourse){

        long status = courseService.newContentNotice(token.getName(), data, idCourse);

        if (status == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do have not permission!");
        else if (status == -2)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        else if (status == -3)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found!");
        else
            return courseService.getContentById(status);
    }


    @GetMapping("/init")
    @ResponseStatus(HttpStatus.OK)
    public void initDB(){
        courseService.initDB();
    }
}
