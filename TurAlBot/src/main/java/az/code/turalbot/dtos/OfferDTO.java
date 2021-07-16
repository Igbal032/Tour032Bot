package az.code.turalbot.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO implements Serializable {
    private Long chatId;
    private Integer messageId;
    private String CompanyName;
    private String UUID;
    private byte[] file;
}
