package cz.mendelu.xlinek.eduapp.api.course.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Link extends Content {
    @Column(name = "link", nullable = false, columnDefinition = "TEXT")
    private String link;
    @Column(name = "btn_title")
    private String btn_title;
    @Column(name = "btn_color")
    private String btn_color;

}
