package cz.mendelu.xlinek.eduapp.api.test.explanation;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Explanation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idExplanation")
    private long id;

    @JsonProperty("content")

    @ManyToOne
    @JoinColumn(name = "testContent", foreignKey = @ForeignKey(name="FK_ETEST_CONTENT"))
    private TestContent testContent;

    @Override
    public String toString() {
        return "Explanation{" +
                "id=" + id +
                ", testContent=" + testContent +
                '}';
    }
}
