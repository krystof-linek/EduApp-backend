package cz.mendelu.xlinek.eduapp.api.test.record;

import com.fasterxml.jackson.annotation.*;
import cz.mendelu.xlinek.eduapp.api.test.answer.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class RecordAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)

    @Column(name = "idRecordAnswer")
    private long id;

    @Column(name = "selectedValue")
    private boolean selectedValue = false;

    @JsonIgnore //doslo by k zacykleni

    @ManyToOne
    @JoinColumn(name = "idRecordTest", foreignKey = @ForeignKey(name="FK_RECORD_A_RECORD_T"))
    private RecordTest recordTest;

    @JsonIgnore

    @OneToOne
    @JoinColumn(name = "idAnswer", foreignKey = @ForeignKey(name="FK_RECORD_A_ANSWER"))
    private Answer answer;

    @Override
    public String toString() {
        return "RecordAnswer{" +
                "id=" + id +
                ", selectedValue=" + selectedValue +
                ", recordTest=" + recordTest +
                ", answer=" + answer +
                '}';
    }
}
