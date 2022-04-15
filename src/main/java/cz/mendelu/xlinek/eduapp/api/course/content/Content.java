package cz.mendelu.xlinek.eduapp.api.course.content;

import com.fasterxml.jackson.annotation.*;
import cz.mendelu.xlinek.eduapp.api.course.Course;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Content {
    @JsonProperty("id_content")

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idContent")
    private long id;

    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "sequence", nullable = false)
    private int sequence = 0;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name", scope = ContentType.class)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("content_type")

    @OneToOne
    @JoinColumn(name = "idContType", foreignKey = @ForeignKey(name="FK_CONT_TYPE"))
    private ContentType contentType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_course", nullable=false)
    private Course course;

    public Content(String title, String description, int sequence, ContentType contentType, Course course){
        this.title = title;
        this.description = description;
        this.sequence = sequence;
        this.contentType = contentType;
        this.course = course;
    }
}
