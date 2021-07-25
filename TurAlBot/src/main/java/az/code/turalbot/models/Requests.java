package az.code.turalbot.models;

import az.code.turalbot.enums.RequestStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


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
    private LocalDateTime createdDate;
}
