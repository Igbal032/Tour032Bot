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
@Table(name = "Offers")
public class Offer{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String UUID;
   private Long chatId;
   private String companyName;
   private String imgPath;
   private Integer messageId;
   private boolean isShow;
   private byte[] file;
   @CreationTimestamp
   private LocalDateTime createdDate;
}
