package az.code.turalbot.cache;

import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageCache implements Serializable {
    private Long chatId;
    private String UUID;
    private int countOfAllImages;
    private boolean isAccess;
    private Integer buttonId;
    private int countOfSendingImage;
}
