package cz.mendelu.xlinek.eduapp.api.course;

import cz.mendelu.xlinek.eduapp.api.course.content.*;
import cz.mendelu.xlinek.eduapp.api.course.content.Link;
import cz.mendelu.xlinek.eduapp.api.course.content.ListItem;
import cz.mendelu.xlinek.eduapp.api.course.content.Paragraph;
import cz.mendelu.xlinek.eduapp.api.course.content.Picture;
import cz.mendelu.xlinek.eduapp.api.course.content.Video;
import cz.mendelu.xlinek.eduapp.api.course.content.MyList;
import cz.mendelu.xlinek.eduapp.api.subject.Subject;
import cz.mendelu.xlinek.eduapp.api.subject.SubjectRepository;
import cz.mendelu.xlinek.eduapp.api.subject.SubjectService;
import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.user.UserRepository;
import cz.mendelu.xlinek.eduapp.api.user.UserService;
import cz.mendelu.xlinek.eduapp.utils.TokenInfo;
import cz.mendelu.xlinek.eduapp.utils.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;
    /* ----------- CONTENT REPOSITORIES ------------- */
    @Autowired
    ContentTypeRepository contentTypeRepository;
    @Autowired
    ContentRepository contentRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    UserRepository userRepository;

    /* ----------- PRIVATE FUNCTIONS ------------ */
    /**
     * Funkce overi, jestli ma uzivatel prislusna opraveni.
     * @param token
     * @return vraci true nebo false
     */
    private boolean isTeacherOrAdmin(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);

        if(!tokenInfo.isTokenValid())
            return false;

        String role = userRepository.findUserByEmail(tokenInfo.getEmail()).getRole();
        if (!(role.equals("TEACHER") || role.equals("ADMIN")))
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

    /**
     * Funkce zkontroluje, jestli ma uzivatel prislusna opravenni či nikoliv.
     * @param token autorizacni token
     * @param course kurz pro overeni
     * @return vraci 0 v pripade uspechu.
     */
    private long hasPermissions(String token, Course course){
        if (!isTeacherOrAdmin(token))
            return -403;

        if (course == null)
            return -404;

        TokenInfo tokenInfo = getTokenInfo(token);

        if (course.getUser().getEmail().equals(tokenInfo.getEmail()))
            return 0;

        return -403;
    }

    /* ------------ CONTROLLER FUNCTIONS -------------- */

    /* ------ SELECT ------ */

    /**
     * Funkce vraci informace o konkretnim obsahu na zaklade ID.
     * @param id ID obsahu.
     * @return vraci prislusny zaznam nebo null pokud neexistuje
     */
    protected Content getContentById(long id){
        return contentRepository.findById(id);
    }

    /**
     * Funkce vraci veskere kurzy, ktere jsou prirazeny k urcitemu predmetu
     * @param idSubject id predmetu
     * @return vraci pole odpovidajicich zaznamu
     */
    protected List<Course> getAllCoursesBySubjectId(long idSubject) {
        return courseRepository.findAllBySubject_IdSubjectOrderByTitle(idSubject);
    }

    /**
     * Funkce vraci veskere kurzy, ktere vytvoril autor na zaklade emailu z tokenu
     * @param token autorizacni token
     * @return v pripade neopravneni vraci null jinak vraci pole prislusnych zaznamu
     */
    protected List<Course> getAllCoursesFromAuthor(String token) {
        if(!isTeacherOrAdmin(token)) //nejsou prislusna opravneni
            return null;

        TokenInfo tokenInfo = getTokenInfo(token);

        return courseRepository.findAllByUserEmail(tokenInfo.getEmail());
    }

    /* ------ INSERT ------ */

    /* ---- DELETE ---- */

    /**
     * Funkce slouzi ke smazani kurzu, ktery si uzivatel vytvoril.
     * Kontroluje se zda je uzivatel vlastnikem kurzu,
     * @param token autorizacni token
     * @param id id kurzu
     * @return v pripade uspechu vraci 0, jinak chybovy kod
     */

    protected long deleteOwnCourseById(String token, long id) {
        Course course = courseRepository.findById(id);

        long status = hasPermissions(token, course);

        if (status != 0)
            return status;

        List<Content> contents = contentRepository.findAllByCourse(course);

        for (Content content: contents) {
            contentRepository.delete(content);
        }

        courseRepository.delete(course);

        return 0;
    }

    /**
     * Funkce slouzi k vytvoreni noveho kurzu.
     * @param token autorizacni token
     * @param data data z pozadavku v prislusnem formatu
     * @return vraci ID nove vytvoreneho zaznamu
     */
    protected long newCourse(String token, CourseController.DataNewCourse data) {

        if(!isTeacherOrAdmin(token)) //nejsou prislusna opravneni
            return -1;

        TokenInfo tokenInfo = getTokenInfo(token);

        if (courseRepository.findByTitleEquals(data.getCourse_title()) != null) //kurz jiz existuje
            return -2;

        Course course = new Course();

        String sub_title = data.getSubject_title();
        sub_title = sub_title.toLowerCase();
        sub_title = sub_title.substring(0, 1).toUpperCase() + sub_title.substring(1);

        Subject subject = subjectRepository.findByTitleAndAndGrade(sub_title, data.getGrade());

        if (subject == null){
            Subject sub = new Subject();
            sub.setTitle(data.getSubject_title());
            sub.setGrade(data.getGrade());
            subject = subjectRepository.save(sub);
        }

        course.setTitle(data.getCourse_title());
        course.setSubject(subject);
        course.setUser(userRepository.findUserByEmail(tokenInfo.getEmail()));

        return courseRepository.save(course).getId();
    }
    /**
     * Funkce vytvori novy obsah typu video pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentVideo(String token, CourseController.VideoData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("video");

        if (contentType == null) //typ neexistuje
            return -3;

        Video video = new Video();
        /* MAIN PARAMS */
        video.setCourse(course);
        video.setTitle(data.getTitle());
        video.setDescription(data.getDescription());
        video.setContentType(contentType);
        video.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* VIDEO PARAMS */
        video.setAuthor(data.getAuthor());
        video.setLink(data.getLink());
        video.setInserted(new Timestamp(System.currentTimeMillis()).getTime());

        return contentRepository.save(video).getId();
    }
    /**
     * Funkce vytvori novy obsah typu obrazek pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentPicture(String token, CourseController.PictureData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("picture");

        if (contentType == null) //typ neexistuje
            return -3;

        Picture picture = new Picture();
        /* MAIN PARAMS */
        picture.setCourse(course);
        picture.setTitle(data.getTitle());
        picture.setDescription(data.getDescription());
        picture.setContentType(contentType);
        picture.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* PICTURE PARAMS */
        picture.setAuthor(data.getAuthor());
        picture.setAlt(data.getAlt());
        picture.setLink(data.getLink());
        picture.setInserted(new Timestamp(System.currentTimeMillis()).getTime());

        return  contentRepository.save(picture).getId();
    }
    /**
     * Funkce vytvori novy obsah typu odstavec pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentParagraph(String token, CourseController.ParagraphData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("paragraph");

        if (contentType == null) //typ neexistuje
            return -3;

        Paragraph paragraph = new Paragraph();
        /* MAIN PARAMS */
        paragraph.setCourse(course);
        paragraph.setTitle(data.getTitle());
        paragraph.setDescription(data.getDescription());
        paragraph.setContentType(contentType);
        paragraph.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* PARAGRAPH PARAMS */
        paragraph.setText(data.getText());

        return  contentRepository.save(paragraph).getId();
    }

    /**
     * Funkce vytvori novy obsah typu nadpis pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentTitle(String token, CourseController.TitleData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("title");

        if (contentType == null) //typ neexistuje
            return -3;

        Title title = new Title();
        /* MAIN PARAMS */
        title.setCourse(course);
        title.setTitle(data.getTitle());
        title.setContentType(contentType);
        title.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* TITLE PARAMS */
        title.setTitle_size(data.getTitle_size());
        title.setIcon(data.getIcon());
        title.setIcon_color(data.getIcon_color());

        return  contentRepository.save(title).getId();
    }

    /**
     * Funkce vytvori novy obsah typu list pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentList(String token, CourseController.ListData data, long idCourse) {

        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("list");

        if (contentType == null) //typ neexistuje
            return -3;

        MyList list = new MyList();
        /* MAIN PARAMS */
        list.setCourse(course);
        list.setTitle(data.getTitle());
        list.setDescription(data.getDescription());
        list.setContentType(contentType);
        list.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* LIST PARAMS */

        int sequence = list.getItems().size() + 1;

        for(int i=0; i < data.getItems().size(); i++){
            ListItem item = new ListItem();
            item.setSequence(sequence++);
            item.setText(data.getItems().get(i));
            list.getItems().add(item);
        }

        if(data.isNumbered())
            list.setNumbered(true);
        else
            list.setNumbered(false);

        return  contentRepository.save(list).getId();
    }
    /**
     * Funkce vytvori novy obsah typu link pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentLink(String token, CourseController.LinkData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("link");

        if (contentType == null) //typ neexistuje
            return -3;

        Link link = new Link();
        /* MAIN PARAMS */
        link.setCourse(course);
        link.setTitle(data.getTitle());
        link.setDescription(data.getDescription());
        link.setContentType(contentType);
        link.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* LIST PARAMS */
        link.setLink(data.getLink());
        link.setBtn_color(data.getBtn_color());
        link.setBtn_title(data.getBtn_title());

        return  contentRepository.save(link).getId();
    }

    /**
     * Funkce vytvori novy obsah typu upozornění pro prislusny kurz.
     * @param token autorizacni token
     * @param data prislusna data o obsahu
     * @param idCourse kurz, kteremu bude prirazen obsah
     * @return vraci ID noveho zaznamu
     */
    public long newContentNotice(String token, CourseController.NoticeData data, long idCourse) {
        if(!isTeacherOrAdmin(token))
            return -1;

        Course course = courseRepository.findById(idCourse);

        if (course == null) //kurz neexistuje
            return -2;

        ContentType contentType = contentTypeRepository.findByName("notice");

        if (contentType == null) //typ neexistuje
            return -3;

        Notice notice = new Notice();
        /* MAIN PARAMS */
        notice.setCourse(course);
        notice.setTitle(data.getTitle());
        notice.setDescription(data.getDescription());
        notice.setContentType(contentType);
        notice.setSequence(contentRepository.findAllByCourse(course).size() + 1);
        /* NOTICE PARAMS */
        notice.setColor(data.getColor());
        notice.setIcon(data.getIcon());

        return  contentRepository.save(notice).getId();
    }

    protected Course getCourseById(long id){
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    protected List<Content> getCourseContentById(long idCourse) {

        Course course = courseRepository.findById(idCourse);

        if (course == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found!");

        return contentRepository.findAllByCourse_IdOrderBySequence(idCourse);
    }

}
