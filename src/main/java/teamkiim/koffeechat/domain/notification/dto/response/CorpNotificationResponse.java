package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
public class CorpNotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String title;

    private String content;

    private Long url;  //현직자 인증 pk

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static CorpNotificationResponse of(String receiverId, Notification notification, long unreadNotifications) {

        return CorpNotificationResponse.builder()
                .receiverId(receiverId)
                .unreadNotifications(unreadNotifications)
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrlPK())
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();


    }
}
