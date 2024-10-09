package teamkiim.koffeechat.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImageUrl;

    private String title;

    private String content;

    private Long url;

    private Long commentId;

    private PostCategory postType;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationResponse of(String receiverId, String senderId, Notification notification, long unreadNotifications) {

        if (notification.getNotificationType() == NotificationType.CORP) { //현직자 인증 알림의 경우 sender x
            return NotificationResponse.builder()
                    .receiverId(receiverId)
                    .unreadNotifications(unreadNotifications)
                    .senderId(null)
                    .senderNickname(null)
                    .profileImageUrl(null)
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(notification.getUrlPK())
                    .postType(notification.getPostType())
                    .commentId(notification.getCommentId())
                    .isRead(false)
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        } else {
            return NotificationResponse.builder()
                    .receiverId(receiverId)
                    .unreadNotifications(unreadNotifications)
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImageUrl(notification.getSender().getProfileImageUrl())
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(notification.getUrlPK())
                    .postType(notification.getPostType())
                    .commentId(notification.getCommentId())
                    .isRead(false)
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        }
    }
}
