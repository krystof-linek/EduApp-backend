package cz.mendelu.xlinek.eduapp.api.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.mendelu.xlinek.eduapp.api.subject.Subject;
import cz.mendelu.xlinek.eduapp.api.test.question.Question;
import cz.mendelu.xlinek.eduapp.api.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idTest")
    private long id;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "active")
    private boolean active = false;

    @Column(name = "open")
    private boolean open = false;

    @Column(name = "old")
    private boolean old = false;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "idSubject", foreignKey = @ForeignKey(name="FK_TEST_SUBJECT"))
    private Subject subject;

    @JsonProperty("author")
    @ManyToOne
    @JoinColumn(name = "idAuthor", foreignKey = @ForeignKey(name="FK_TEST_USER"))
    private User user;

    //@OneToMany(mappedBy = "id")
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Question.class)
    @OnDelete(action = OnDeleteAction.CASCADE)

    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", active=" + active +
                ", open=" + open +
                ", old=" + old +
                ", created=" + created +
                ", updated=" + updated +
                ", subject=" + subject +
                ", user=" + user +
                ", questions=" + questions +
                '}';
    }
}
