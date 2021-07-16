package az.code.turalbot.cache;

import az.code.turalbot.models.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Cash {
    private RedisTemplate redisTemplate;

    public Cash(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String hashKey = "imageCount";

    public Integer save(Long chatId,int count){
        redisTemplate.opsForHash().put(hashKey,chatId,count);
        return count;
    }

    public void reset(Long chatId){
        redisTemplate.opsForHash().put(hashKey,chatId,0);
    }

    public Integer findByChatId(Long chatId){
        Integer count = (Integer) redisTemplate.opsForHash().get(hashKey, chatId);
        return count;
    }
}
