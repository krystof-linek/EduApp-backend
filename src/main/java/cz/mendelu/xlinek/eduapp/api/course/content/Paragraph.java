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
public class Paragraph extends Content {
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
}
