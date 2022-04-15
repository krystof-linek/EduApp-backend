package cz.mendelu.xlinek.eduapp.api.user;

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
public class Parent extends User {
    @ManyToMany()

    @JoinTable(name = "parent_kids")
    private List<User> kids = new ArrayList<>();
}
