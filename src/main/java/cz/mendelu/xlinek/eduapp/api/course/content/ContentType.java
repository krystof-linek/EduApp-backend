package cz.mendelu.xlinek.eduapp.api.course.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ContentType {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idContType")
    private int id;
    @Column(name = "name", nullable = false)
    private String name;

    public ContentType(String name){
        this.name = name;
    }
}
