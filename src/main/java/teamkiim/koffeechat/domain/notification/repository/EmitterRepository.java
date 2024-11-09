package teamkiim.koffeechat.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.domain.notification.service.emitter.SseEmitterWrapper;

@Component
public class EmitterRepository {

    //서버와 클라이언트 간의 연결성을 저장한다.
    private final Map<String, SseEmitterWrapper> emitters = new ConcurrentHashMap<>();

    public SseEmitterWrapper save(String emitterId, SseEmitterWrapper sseEmitterWrapper) {
        emitters.put(emitterId, sseEmitterWrapper);
        return sseEmitterWrapper;
    }

    public Map<String, SseEmitterWrapper> findAllEmitterByReceiverId(String encryptedMemberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(encryptedMemberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 알림 수신 받는 emitter 찾기
    public Map<String, SseEmitterWrapper> findReceiveEmitterByReceiverId(String encryptedMemberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(encryptedMemberId) && entry.getValue()
                        .isReceive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

}