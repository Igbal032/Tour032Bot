package az.code.turalbot.cache;


public interface SessionCache {
    Session save(Session session);
    void delete(Session session);
    Session findByChatId(Long chatId);
}
