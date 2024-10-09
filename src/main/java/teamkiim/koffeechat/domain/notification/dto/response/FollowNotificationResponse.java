package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
public class FollowNotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImagePath;
    private String profileImageName;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static FollowNotificationResponse of(String receiverId, String senderId, Notification notification, long unreadNotifications) {

        return FollowNotificationResponse.builder()
                .receiverId(receiverId)
                .unreadNotifications(unreadNotifications)
                .senderId(senderId)
                .senderNickname(notification.getSender().getNickname())
                .profileImagePath(notification.getSender().getProfileImagePath())
                .profileImageName(notification.getSender().getProfileImageName())
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }
}
