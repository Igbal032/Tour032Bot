package az.code.turalbot.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Offer implements Serializable{
   private Long chatId;
   private Long messageId;
   private byte[] file;
}
