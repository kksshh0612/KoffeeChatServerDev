package teamkiim.koffeechat.domain.notification.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

@Getter
@Builder
public class FollowNotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String senderProfileImageUrl;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static FollowNotificationResponse of(String receiverId, String senderId, Notification notification) {

        return FollowNotificationResponse.builder()
                .receiverId(receiverId)
                .unreadNotifications(notification.getReceiver().getUnreadNotificationCount())
                .senderId(senderId)
                .senderNickname(notification.getSender().getNickname())
                .senderProfileImageUrl(notification.getSender().getProfileImageUrl())
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }
}
