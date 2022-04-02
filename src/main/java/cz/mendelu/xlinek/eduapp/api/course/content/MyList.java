package cz.mendelu.xlinek.eduapp.api.course.content;

import cz.mendelu.xlinek.eduapp.api.course.content.Content;
import cz.mendelu.xlinek.eduapp.api.course.content.ListItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class MyList extends Content {
    @Column(name = "numbered")
    private boolean numbered = false;
    @Column(name = "sequence", nullable = false)
    private int sequence = 0;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<ListItem> items = new ArrayList<>();
}
