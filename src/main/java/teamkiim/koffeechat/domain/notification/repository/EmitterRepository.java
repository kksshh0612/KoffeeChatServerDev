package teamkiim.koffeechat.domain.notification.repository;

import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.domain.notification.domain.SseEmitterWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {

    //서버와 클라이언트 간의 연결성을 저장한다.
    private final Map<String, SseEmitterWrapper> emitters = new ConcurrentHashMap<>();

    public SseEmitterWrapper save(String emitterId, SseEmitterWrapper sseEmitterWrapper) {
        emitters.put(emitterId, sseEmitterWrapper);
        return sseEmitterWrapper;
    }

    public Map<String, SseEmitterWrapper> findAllEmitterByReceiverId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 알림 수신 받는 emitter 찾기
    public Map<String, SseEmitterWrapper> findReceiveEmitterByReceiverId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId) && entry.getValue().isReceive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

}
