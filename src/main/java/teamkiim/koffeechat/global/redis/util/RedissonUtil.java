package teamkiim.koffeechat.global.redis.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonUtil {

    private final RedissonClient redissonClient;

    public void setData(String prefix, String key, String value, long duration) {
        redissonClient.getBucket(key).set(prefix + value, Duration.ofMillis(duration));
    }

    public String getData(String key) {
        return (String) redissonClient.getBucket(key).get();
    }

    public void deleteData(String key) {
        redissonClient.getBucket(key).delete();
    }

    public boolean hashKey(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    public void setData(String prefix, Long key, Long value) {
        RSet<Long> values = redissonClient.getSet(prefix + key);
        values.add(value);
    }

    public void deleteData(String prefix, Long key, Long value) {
        RSet<Long> values = redissonClient.getSet(prefix + key);
        values.remove(value);
    }

    public List<Long> getDataList(String prefix, Long key) {
        RSet<Long> values = redissonClient.getSet(prefix + key);
        return new ArrayList<>(values.readAll());
    }

}
