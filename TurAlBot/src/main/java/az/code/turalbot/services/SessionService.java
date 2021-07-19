package az.code.turalbot.services;

import az.code.turalbot.models.Action;
import az.code.turalbot.models.Language;
import az.code.turalbot.cache.Session;

import java.util.Map;

public interface SessionService {
    Session findByChatId(Long chatId);
    void delete(Session session);
    Session saveSession(long chatId, Action action, Boolean isProgress,
                        Language language, Map<String,String> answers,
                        boolean isNew);
    void updateSession(Long chatId,Action action, Boolean isProgress,
                       Language language, Map<String,String> answers);
}
