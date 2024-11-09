package teamkiim.koffeechat.domain.notification.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class SseEmitterWrapper {

    private SseEmitter sseEmitter;
    private List<ChatRoomNotificationStatus> chatRoomNotificationStatusList;  // 사용자가 채팅방 별로 접속해있는 상태 저장
    private boolean isReceive;  // 채팅에 대한 알림 수신 여부

    @Builder
    public SseEmitterWrapper(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
        this.chatRoomNotificationStatusList = new ArrayList<>();
        this.isReceive = false;
    }

    // emitter 최초 생성 후 채팅방 목록 초기화
    public void updateChatRoomNotificationStatus(List<Long> memberChatRoomIdList) {
        this.chatRoomNotificationStatusList = memberChatRoomIdList.stream()
                .map(id -> new ChatRoomNotificationStatus(id, true))
                .collect(Collectors.toList());
    }

    // 채팅 알림 sse 수신 o/x
    public void startReceiving() {
        this.isReceive = true;
    }

    public void stopReceiving() {
        this.isReceive = false;
    }

    //채팅방 입장/퇴장 시 알림 설정 추가
    public void addChatRoomNotificationStatus(Long chatRoomId) {
        boolean exists = chatRoomNotificationStatusList.stream()
                .anyMatch(status -> status.getChatRoomId().equals(chatRoomId));  //이미 채팅방 목록에 존재하는 채팅방인지 검사

        if (!exists) {
            this.chatRoomNotificationStatusList.add(
                    new ChatRoomNotificationStatus(chatRoomId, false));  //채팅방 입장 후 websocket 통신
        }
    }

    public void removeChatRoomNotificationStatus(Long chatRoomId) {

        this.chatRoomNotificationStatusList.removeIf(status -> status.getChatRoomId().equals(chatRoomId));
    }

    //채팅방 접속/미접속 시 sse 알림 상태 on/off
    public void onChatRoomNotificationStatus(Long chatRoomId) {  //채팅방 미접속시 : sse 알림 on
        this.chatRoomNotificationStatusList.stream()
                .filter(status -> status.getChatRoomId().equals(chatRoomId))
                .findFirst()
                .ifPresent(ChatRoomNotificationStatus::startSseAlert);
    }

    public void offChatRoomNotificationStatus(Long chatRoomId) {  //채팅방 접속시 : sse 알림 off
        this.chatRoomNotificationStatusList.stream()
                .filter(status -> status.getChatRoomId().equals(chatRoomId))
                .findFirst()
                .ifPresent(ChatRoomNotificationStatus::stopSseAlert);
    }

    //해당 채팅방에 대한 알림 수신 여부 확인
    public boolean isSseAlertActive(Long chatRoomId) {
        return chatRoomNotificationStatusList.stream()
                .filter(status -> status.getChatRoomId().equals(chatRoomId))
                .findFirst()
                .map(ChatRoomNotificationStatus::isAlert)  // isAlert 상태 반환
                .orElse(false);
    }
}