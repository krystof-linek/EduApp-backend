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
public class Notice extends Content{
    @Column(name = "color")
    private String color;
    @Column(name = "icon")
    private String icon;
}
