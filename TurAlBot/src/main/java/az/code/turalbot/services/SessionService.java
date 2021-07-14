package az.code.turalbot.services;

import az.code.turalbot.models.Action;
import az.code.turalbot.models.Language;
import az.code.turalbot.models.Session;

import java.util.Map;

public interface SessionService {
    Session saveSession(long chatId, Action action, Boolean isProgress, Language language, Map<String,String> answers, boolean isNew);
    Session findByChatId(Long chatId);
    void delete(Session session);
    void updateSession(Long chatId,Action action, Boolean isProgress,Language language, Map<String,String> answers);

}
