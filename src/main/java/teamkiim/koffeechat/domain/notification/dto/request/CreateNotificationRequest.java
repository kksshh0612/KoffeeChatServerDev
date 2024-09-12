package teamkiim.koffeechat.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 생성 Request")
public class CreateNotificationRequest {

    private Member sender;

    private String title;

    private String content;

    private Long url;

    private boolean isRead;

    private NotificationType notificationType;

    public static CreateNotificationRequest of(Member sender, String title, String content, Long pk, NotificationType notificationType) {
        return CreateNotificationRequest.builder()
                .sender(sender)
                .title(title)
                .content(content)
                .url(pk)
                .isRead(false)
                .notificationType(notificationType)
                .build();
    }

    public Notification toEntity(String eventId, Member receiver) {
        return Notification.builder()
                .eventId(eventId)
                .receiver(receiver)
                .sender(sender)
                .title(title)
                .content(content)
                .url(url)
                .notificationType(notificationType)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
