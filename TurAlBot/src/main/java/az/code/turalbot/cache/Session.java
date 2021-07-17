package az.code.turalbot.cache;

import az.code.turalbot.models.Action;
import az.code.turalbot.models.Language;
import az.code.turalbot.models.Question;
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