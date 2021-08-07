package az.code.Tour032.cache;


public interface SessionCache {
    Session save(Session session);
    void delete(Session session);
    Session findByChatId(Long chatId);
}
