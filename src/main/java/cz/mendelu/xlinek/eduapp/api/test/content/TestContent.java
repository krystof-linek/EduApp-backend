package cz.mendelu.xlinek.eduapp.api.test.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class TestContent {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idTestContent")
    private long id;
    @Column(name = "picture")
    private String picture;
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    @Column(name = "equation")
    private String equation;

    public TestContent(String text, String picture, String equation){
        this.text = text;
        this.picture = picture;
        this.equation = equation;
    }

    @Override
    public String toString() {
        return "TestContent{" +
                "id=" + id +
                ", picture='" + picture + '\'' +
                ", text='" + text + '\'' +
                ", equation='" + equation + '\'' +
                '}';
    }
}
