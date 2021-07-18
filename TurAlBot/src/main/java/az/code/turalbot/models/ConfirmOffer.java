package az.code.turalbot.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.*;
import java.io.Serializable;
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
    private String phoneOrUserName;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
