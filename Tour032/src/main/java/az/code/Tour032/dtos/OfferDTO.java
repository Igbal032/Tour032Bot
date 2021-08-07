package az.code.Tour032.dtos;

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
    private Long agentId;
    private ImageDTO imageDTO;
    private String UUID;
    private byte[] file;
}
