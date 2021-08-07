package az.code.Tour032.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "requests")
public class Requests implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String UUID;
    private Long chatId;
    private String jsonText;
    private boolean isActive;
    private String requestStatus;
    private LocalDateTime expiredDate;
    private LocalDateTime createdDate;
}
