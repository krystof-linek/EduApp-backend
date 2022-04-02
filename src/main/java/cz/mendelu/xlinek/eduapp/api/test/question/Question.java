package cz.mendelu.xlinek.eduapp.api.test.question;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import cz.mendelu.xlinek.eduapp.api.course.content.ContentType;
import cz.mendelu.xlinek.eduapp.api.test.Test;
import cz.mendelu.xlinek.eduapp.api.test.answer.Answer;
import cz.mendelu.xlinek.eduapp.api.test.content.TestContent;
import cz.mendelu.xlinek.eduapp.api.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idQuestion")
    private long id;
    @Column(name = "sequence")
    private int sequence;

    @JsonProperty("content")

    @ManyToOne
    @JoinColumn(name = "testContent", foreignKey = @ForeignKey(name="FK_QTEST_CONTENT"))
    private TestContent testContent;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Test.class)
    @JsonProperty("id_test")
    @JsonIdentityReference(alwaysAsId = true)

    @ManyToOne()
    @JoinColumn(name = "id_test")
    private Test test;
/*
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", testContent=" + testContent +
                ", answers=" + answers +
                '}';
    }

 */
}
