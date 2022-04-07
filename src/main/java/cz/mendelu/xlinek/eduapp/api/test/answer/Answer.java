package cz.mendelu.xlinek.eduapp.api.test.answer;

import com.fasterxml.jackson.annotation.*;
import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.explanation.Explanation;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContent;
import cz.mendelu.xlinek.eduapp.api.test.question.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idAnswer")
    private long id;
    @Column(name = "isTrue")
    private boolean isTrue = false;

    @JsonProperty("content")

    @ManyToOne
    @JoinColumn(name = "testContent", foreignKey = @ForeignKey(name="FK_ATEST_CONTENT"))
    private TestContent testContent;

    @ManyToOne
    @JoinColumn(name = "question", foreignKey = @ForeignKey(name="FK_QUESTION"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Question.class)
    @JsonProperty("id_question")
    @JsonIdentityReference(alwaysAsId = true)

    private Question question;

    @ManyToOne
    @JoinColumn(name = "explanation", foreignKey = @ForeignKey(name="FK_EXPLANATION"))
    private Explanation explanation;

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", isTrue=" + isTrue +
                ", testContent=" + testContent +
                ", question=" + question +
                ", explanation=" + explanation +
                '}';
    }
}
