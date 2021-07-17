package az.code.turalbot.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "buttons")
public class Button {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String buttonText;
    private String buttonCallBack;
    private String keyWord;
    @ManyToOne
    private Question question;
    @ManyToOne
    private Language language;
}
