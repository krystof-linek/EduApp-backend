package cz.mendelu.xlinek.eduapp.api.test.record;

import com.fasterxml.jackson.annotation.*;
import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.answer.Answer;
import cz.mendelu.xlinek.eduapp.api.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class RecordTest {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)

    @JsonProperty("id_record")
    @Column(name = "idRecordTest")
    private long id;

    @ManyToOne
    @JoinColumn(name = "idTest", foreignKey = @ForeignKey(name="FK_RECORD_T_TEST"))
    private Test test;

    private LocalDateTime started;

    private LocalDateTime ended;

    @JsonProperty("student")
    @ManyToOne
    @JoinColumn(name = "idUser", foreignKey = @ForeignKey(name="FK_RECORD_T_USER"))
    private User user;

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = Answer.class)
    private List<Answer> badAnswers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        started = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "RecordTest{" +
                "id=" + id +
                ", test=" + test +
                ", started=" + started +
                ", ended=" + ended +
                ", user=" + user +
                ", badAnswers=" + badAnswers +
                '}';
    }
}
