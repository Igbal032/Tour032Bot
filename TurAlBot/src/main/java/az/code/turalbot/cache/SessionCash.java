package az.code.turalbot.cache;

import az.code.turalbot.models.Session;
import org.springframework.stereotype.Component;


public interface SessionCash {
    Session save(Session session);
    void delete(Session session);
    Session findByChatId(Long chatId);
}
