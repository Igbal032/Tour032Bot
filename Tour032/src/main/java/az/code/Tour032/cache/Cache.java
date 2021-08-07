package az.code.Tour032.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Cache {

    private RedisTemplate redisTemplate;

    public Cache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String hashKey = "ImageCash3";

    public ImageCache save(ImageCache imageCache){
        redisTemplate.opsForHash().put(hashKey, imageCache.getUUID(), imageCache);
        return imageCache;
    }

    public void delete(ImageCache imageCache){
        redisTemplate.opsForHash().delete(hashKey,imageCache.getUUID(),imageCache);
    }

    public ImageCache findByUUID(String UUID){
        ImageCache ImageCache =  (ImageCache) redisTemplate.opsForHash().get(hashKey, UUID);
        return ImageCache;
    }
}
