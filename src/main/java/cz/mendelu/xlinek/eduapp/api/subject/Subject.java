package cz.mendelu.xlinek.eduapp.api.subject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idSubject")
    private int idSubject;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "grade", nullable = false)
    private int grade;
}
