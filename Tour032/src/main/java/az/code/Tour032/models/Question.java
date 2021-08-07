package az.code.Tour032.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "questions")
public class Question  implements Serializable {
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
