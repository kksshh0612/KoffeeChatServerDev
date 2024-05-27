package teamkiim.koffeechat.global.redis.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 저장/조회/삭제를 수행하는 Util 클래스
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    // 데이터 저장. duration 지나면 레디스에서 데이터 자동 삭제. (밀리초)

    /**
     * 데이터 저장 (duration 지나면 레디스에서 데이터 자동 삭제. (밀리초))
     * @param key
     * @param value
     * @param duration 유효 기간
     */
    public void setData(String key, String value, long duration) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);      // 밀리초로 설정
        valueOperations.set(key, value, expireDuration);
    }

    /**
     * 데이터 저장 (duration 지나면 레디스에서 데이터 자동 삭제. (단위 설정 가능))
     * @param key
     * @param value
     * @param duration 유효 기간
     * @param timeUnit 시간 단위
     */
    public void setData(String key, String value, long duration, TimeUnit timeUnit) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, duration, timeUnit);
    }

    /**
     * key에 해당하는 value 조회
     * @param key
     * @return value
     */
    public String getData(String key){

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * key에 해당하는 key-value 삭제
     * @param key
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 키 존재 여부 확인
     * @param key
     * @return 키가 존재할 경우 -> true / 키가 존재하지 않을 경우 -> false
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
