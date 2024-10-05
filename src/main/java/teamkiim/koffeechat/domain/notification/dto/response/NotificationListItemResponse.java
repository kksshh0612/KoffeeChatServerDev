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

    private Long id;

    private String senderId;  //알림 내용에 포함될 회원
    private String senderNickname;
    private String profileImagePath;
    private String profileImageName;

    private String title;

    private String content;

    private Long url;

    private Long commentId;

    private PostCategory postType;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationListItemResponse of(Notification notification, String senderId) {

        if (notification.getNotificationType().equals(NotificationType.FOLLOW)) {
            return NotificationListItemResponse.builder()
                    .id(notification.getId())
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImagePath(notification.getSender().getProfileImagePath())
                    .profileImageName(notification.getSender().getProfileImageName())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();

        } else if (notification.getNotificationType().equals(NotificationType.COMMENT)) {
            return NotificationListItemResponse.builder()
                    .id(notification.getId())
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImagePath(notification.getSender().getProfileImagePath())
                    .profileImageName(notification.getSender().getProfileImageName())
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(notification.getUrlPK())
                    .postType(notification.getPostType())
                    .commentId(notification.getCommentId())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();

        } else if (notification.getNotificationType().equals(NotificationType.POST)) {  // 글 알림
            return NotificationListItemResponse.builder()
                    .id(notification.getId())
                    .senderId(senderId)
                    .senderNickname(notification.getSender().getNickname())
                    .profileImagePath(notification.getSender().getProfileImagePath())
                    .profileImageName(notification.getSender().getProfileImageName())
                    .title(notification.getTitle())
                    .url(notification.getUrlPK())
                    .postType(notification.getPostType())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        } else {  //현직자 인증 알림
            return NotificationListItemResponse.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .url(notification.getUrlPK())
                    .isRead(notification.isRead())
                    .notificationType(notification.getNotificationType())
                    .createdTime(notification.getCreatedTime())
                    .build();
        }
    }

}
