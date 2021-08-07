package az.code.Tour032.services.interfaces;

import az.code.Tour032.models.Action;
import az.code.Tour032.models.Language;
import az.code.Tour032.cache.Session;

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
