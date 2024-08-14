package teamkiim.koffeechat.domain.notification.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

@Getter
@Builder
@Schema(description = "알림 생성 Request")
public class CreateNotificationRequest {

    private Member member;

    private String title;

    private String content;

    private String url;

    private boolean isRead;

    private NotificationType notificationType;

    public static CreateNotificationRequest of(Member member, String title, String content, String url, NotificationType notificationType) {
        return CreateNotificationRequest.builder()
                .member(member)
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .notificationType(notificationType)
                .build();
    }
}
