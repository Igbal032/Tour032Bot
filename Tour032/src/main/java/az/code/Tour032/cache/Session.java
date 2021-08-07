package az.code.Tour032.cache;

import az.code.Tour032.models.Action;
import az.code.Tour032.models.Language;
import az.code.Tour032.models.Question;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    private String UUID;
    private Long chatId;
    private Question currentQuestion;
    private Action currentAction;
    private Language currentLanguage;
    private Map<String, String> questionsAndAnswers = new HashMap<>();
    private Boolean isProgress;
}