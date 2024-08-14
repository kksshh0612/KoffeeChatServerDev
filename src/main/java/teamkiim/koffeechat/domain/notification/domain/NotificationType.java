package teamkiim.koffeechat.domain.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {

    POST("글"),
    COMMENT("댓글"),
    FOLLOW("팔로우");

    private String type;

    NotificationType(String type) {
        this.type=type;
    }
}
