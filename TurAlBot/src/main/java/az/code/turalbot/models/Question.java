package az.code.turalbot.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String keyWord;
    private String regex;
    private String typeOfNotification;
    @OneToMany(mappedBy = "question")
    private List<Button> buttons = new ArrayList<>();
}
