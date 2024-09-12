package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationListItemResponse {

    private Long id;

    private Long senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImagePath;
    private String profileImageName;

    private String title;

    private String content;

    private Long url;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationListItemResponse of(Notification notification) {
        return NotificationListItemResponse.builder()
                .id(notification.getId())
                .senderId(notification.getSender().getId())
                .senderNickname(notification.getSender().getNickname())
                .profileImagePath(notification.getSender().getProfileImagePath())
                .profileImageName(notification.getSender().getProfileImageName())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }

}
