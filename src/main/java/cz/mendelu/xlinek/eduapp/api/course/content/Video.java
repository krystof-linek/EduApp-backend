package cz.mendelu.xlinek.eduapp.api.course.content;

import cz.mendelu.xlinek.eduapp.api.course.content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Video extends Content {
    @Column(name = "author")
    private String author;
    @Column(name = "link", nullable = false)
    private String link;
    @Column(name = "inserted")
    private Long inserted;
}
