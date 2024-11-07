package teamkiim.koffeechat.domain.notification.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

@Getter
@Builder
public class CorpNotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String title;

    private String content;

    private String url;  //현직자 인증 pk

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static CorpNotificationResponse of(String receiverId, String corpId, Notification notification) {

        return CorpNotificationResponse.builder()
                .receiverId(receiverId)
                .unreadNotifications(notification.getReceiver().getUnreadNotificationCount())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(corpId)
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();


    }
}
