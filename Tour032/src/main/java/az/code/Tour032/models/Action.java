package az.code.Tour032.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "actions")
public class Action implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String text;
    private Integer nextId;
    @ManyToOne
    private Question question;
}
