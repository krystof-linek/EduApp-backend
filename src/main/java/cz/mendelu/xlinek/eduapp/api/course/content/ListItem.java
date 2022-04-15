package cz.mendelu.xlinek.eduapp.api.course.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.mendelu.xlinek.eduapp.api.course.content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ListItem {
    @JsonProperty("id_item")

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "idItem")
    private long id;
    @Column(name = "sequence", nullable = false)
    private int sequence = 0;
    @Column(name = "text")
    private String text;
}
