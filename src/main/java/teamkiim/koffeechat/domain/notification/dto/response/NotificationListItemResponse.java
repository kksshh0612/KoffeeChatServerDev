package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationListItemResponse {

    private String id;

    private String senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImageUrl;

    private String title;

    private String content;

    private String url;

    private String commentId;

    private PostCategory postType;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationListItemResponse of(String notificationId, Notification notification, String senderId, String urlPK, String commentId) {

        if (notification.getNotificationType().equals(NotificationType.POST)) {  // 글 알림
            return NotificationListItemResponse.builder()
                    .id(notificationId)
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImageUrl(notification.getSender().getProfileImageUrl())
                    .title(notification.getTitle())
                    .url(urlPK)
                    .postType(notification.getPostType())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        } else if (notification.getNotificationType().equals(NotificationType.COMMENT)) {
            return NotificationListItemResponse.builder()
                    .id(notificationId)
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImageUrl(notification.getSender().getProfileImageUrl())
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(urlPK)
                    .postType(notification.getPostType())
                    .commentId(commentId)
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();

        } else if (notification.getNotificationType().equals(NotificationType.FOLLOW)) {
            return NotificationListItemResponse.builder()
                    .id(notificationId)
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImageUrl(notification.getSender().getProfileImageUrl())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        } else {  //현직자 인증 알림
            return NotificationListItemResponse.builder()
                    .id(notificationId)
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(urlPK)
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        }
    }
}
