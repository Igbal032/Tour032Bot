package az.code.Tour032.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "translate")
public class Translate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String translatedContent;
    @ManyToOne
    private Language language;
    @ManyToOne
    private Question question;
}
