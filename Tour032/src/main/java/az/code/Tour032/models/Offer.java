package az.code.Tour032.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offers")
public class Offer implements Serializable{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String UUID;
   private Long chatId;
   @OneToOne
   private Agent agent;
   private String imgPath;
   private Integer messageId;
   private boolean isShow;
   private byte[] file;
   private String offerStatus;
   private String phoneNumber;
   @CreationTimestamp
   private LocalDateTime createdDate;
}
