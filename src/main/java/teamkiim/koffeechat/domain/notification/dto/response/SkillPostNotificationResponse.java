package teamkiim.koffeechat.domain.notification.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

@Getter
@Builder
public class SkillPostNotificationResponse {

    private String receiverId;  //알림을 받는 회원
    private long unreadNotifications;  //읽지 않은 알림 갯수

    private String title;

    private String url;

    private PostCategory postCategory;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static SkillPostNotificationResponse of(String receiverId, String postId, Notification notification) {
        return SkillPostNotificationResponse.builder()
                .receiverId(receiverId)
                .unreadNotifications(notification.getReceiver().getUnreadNotificationCount())
                .title(notification.getTitle())  //기술 이름
                .url(postId)
                .postCategory(notification.getPostType())
                .isRead(false)
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }
}
