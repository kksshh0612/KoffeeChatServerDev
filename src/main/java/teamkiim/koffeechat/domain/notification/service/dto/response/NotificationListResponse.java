package teamkiim.koffeechat.domain.notification.service.dto.response;

import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.domain.Notification;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationListResponse {

    private Long id;

    private Long memberId;  //알림 내용에 포함될 회원
    private String memberNickname;
    private String profileImagePath;
    private String profileImageName;

    private String title;

    private String content;

    private String url;

    private boolean isRead;

    private NotificationType notificationType;

    private LocalDateTime createdTime;

    public static NotificationListResponse of(Notification notification) {
        return NotificationListResponse.builder()
                .id(notification.getId())
                .memberId(notification.getMember().getId())
                .memberNickname(notification.getMember().getNickname())
                .profileImagePath(notification.getMember().getProfileImagePath())
                .profileImageName(notification.getMember().getProfileImageName())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .notificationType(notification.getNotificationType())
                .createdTime(notification.getCreatedTime())
                .build();
    }

}
