package az.code.Tour032.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "confirm_offer")
public class ConfirmOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String UUID;
    private Long chatId;
    private String companyName;
    private byte[] file;
    private String phoneNumber;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
