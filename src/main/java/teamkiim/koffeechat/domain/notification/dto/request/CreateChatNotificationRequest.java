package teamkiim.koffeechat.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.post.common.domain.Post;

@Getter
@Builder
@Schema(description = "채팅 알림 생성 Request")
public class CreateChatNotificationRequest {

    private Member sender;

    private String title;

    private String content;

    private Long roomId;

    private NotificationType notificationType;

    public static CreateChatNotificationRequest of(NotificationType notificationType, Member sender) {
        return CreateChatNotificationRequest.builder()
                .sender(sender)
                .roomId(0L)
                .title("알림 내용")
                .content("알림 내용")
                .notificationType(NotificationType.CHATTING)
                .build();
    }
}
