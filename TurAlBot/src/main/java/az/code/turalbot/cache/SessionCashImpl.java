package az.code.turalbot.cache;

import az.code.turalbot.models.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class SessionCashImpl implements SessionCash {

    private  RedisTemplate redisTemplate;

    public SessionCashImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String hashKey = "session";
    public Session save(Session session){
        redisTemplate.opsForHash().put(hashKey,session.getChatId(),session);
        System.out.println(findByChatId(session.getChatId()).getCurrentLanguage().getLanguageName()+"  - salam");
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
