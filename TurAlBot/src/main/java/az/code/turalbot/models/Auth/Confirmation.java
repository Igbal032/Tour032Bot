package az.code.turalbot.models.Auth;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "confirmation")
public class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @Column(name = "confirm_number")
    private int confirmNumber;
    private String emailStatus;
    @CreationTimestamp
    private LocalDateTime createdDate;
    private LocalDateTime expiredDate;
}
