package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private Long senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImagePath;
    private String profileImageName;

    private String title;

    private String content;

    private String url;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationResponse of(Notification notification, long unreadNotifications) {
        return NotificationResponse.builder()
                .receiverId(notification.getReceiver().getId())
                .unreadNotifications(unreadNotifications)
                .senderId(notification.getSender().getId())
                .senderNickname(notification.getSender().getNickname())
                .profileImagePath(notification.getSender().getProfileImagePath())
                .profileImageName(notification.getSender().getProfileImageName())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }
}
