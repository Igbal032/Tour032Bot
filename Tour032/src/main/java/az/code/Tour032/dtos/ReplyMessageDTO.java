package az.code.Tour032.dtos;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMessageDTO {
    private String UUID;
    Integer messageId;
    String phoneNumber;
}
