package cz.mendelu.xlinek.eduapp.api.course.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Title extends Content {
    @Column(name = "title_size")
    private int title_size = 1;
    @Column(name = "icon")
    private String icon = "";
    @Column(name = "icon_color")
    private String icon_color= "";
}
