package cz.mendelu.xlinek.eduapp.api.course;

import cz.mendelu.xlinek.eduapp.api.course.content.Content;
import cz.mendelu.xlinek.eduapp.api.subject.Subject;
import cz.mendelu.xlinek.eduapp.api.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idCourse")
    private long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "created")
    private LocalDateTime created;
    @OneToOne
    @JoinColumn(name = "idSubject", foreignKey = @ForeignKey(name="FK_SUBJECT"))
    private Subject subject;
    @OneToOne
    @JoinColumn(name = "idAuthor", foreignKey = @ForeignKey(name="FK_USER"))
    private User user;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
}
