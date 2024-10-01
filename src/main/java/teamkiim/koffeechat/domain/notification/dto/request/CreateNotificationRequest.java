package teamkiim.koffeechat.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 생성 Request")
public class CreateNotificationRequest {

    private Member sender;

    private String title;

    private String content;

    private Long urlPK;  //urlPK

    private Long commentId;  //commentId

    private PostCategory postType; //postType

    private boolean isRead;

    private NotificationType notificationType;

    public static CreateNotificationRequest ofForPost(NotificationType notificationType, Member sender, Post post) {
        return CreateNotificationRequest.builder()
                .sender(sender)
                .urlPK(post.getId())
                .title(post.getTitle())
                .postType(post.getPostCategory())
                .notificationType(notificationType)
                .build();
    }

    public static CreateNotificationRequest ofForComment(NotificationType notificationType, Member sender, Post post, Comment comment) {
        return CreateNotificationRequest.builder()
                .sender(sender)
                .urlPK(post.getId())
                .title(post.getTitle())
                .content(comment.getContent())
                .commentId(comment.getId())
                .postType(post.getPostCategory())
                .notificationType(notificationType)
                .build();
    }

    public static CreateNotificationRequest ofForFollow(NotificationType notificationType, Member sender) {
        return CreateNotificationRequest.builder()
                .sender(sender)
                .notificationType(notificationType)
                .build();
    }

    public static CreateNotificationRequest ofForCorp(NotificationType notificationType, Corp corp, Verified verified) {
        return CreateNotificationRequest.builder()
                .urlPK(corp.getId())
                .title(corp.getName())
                .content(verified.getStatus())
                .notificationType(notificationType)
                .build();
    }

    public Notification toEntity(String eventId, Member receiver) {
        return Notification.builder()
                .eventId(eventId)
                .receiver(receiver)
                .sender(sender)
                .urlPK(urlPK)
                .title(title)
                .content(content)
                .commentId(commentId)
                .postType(postType)
                .notificationType(notificationType)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
