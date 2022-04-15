package cz.mendelu.xlinek.eduapp.api.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter @Getter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "edu_sequence")
    @TableGenerator(name = "edu_sequence", table = "seq_table", pkColumnName = "entity", valueColumnName = "seq_value", initialValue = 0, allocationSize = 1)
    @Column(name = "id_users")
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "registered")
    private LocalDateTime registered;

    @Column(name = "role")
    private String role;

    @Column(name = "grade")
    private int grade;

    @Column(name = "classRoom")
    private String classRoom;

    @Column(name = "picture")
    private String picture;

    @Column(name = "validated")
    private boolean validated = false;


    public User(String email, String name, String surname, String role, int grade, String classRoom) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.grade = grade;
        this.classRoom = classRoom;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", registered=" + registered +
                ", role='" + role + '\'' +
                ", grade=" + grade +
                ", classRoom='" + classRoom + '\'' +
                ", picture='" + picture + '\'' +
                ", validated=" + validated +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        registered = LocalDateTime.now();
    }
}
