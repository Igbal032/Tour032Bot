package az.code.Tour032.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionCacheImpl implements SessionCache {

    private  RedisTemplate redisTemplate;

    public SessionCacheImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String hashKey = "session3";
    public Session save(Session session){
        redisTemplate.opsForHash().put(hashKey,session.getChatId(),session);
        return session;
    }

    public void delete(Session session){
        redisTemplate.opsForHash().delete(hashKey,session.getChatId(),session);
    }

    public Session findByChatId(Long chatId){
        Session session = (Session) redisTemplate.opsForHash().get(hashKey, chatId);
        return session;
    }
}
