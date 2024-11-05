package teamkiim.koffeechat.domain.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {

    ALL("전체"),
    POST("글"),
    TECH_POST("기술게시글"),
    COMMENT("댓글"),
    FOLLOW("팔로우"),
    CORP("현직자인증"),
    CHATTING("채팅");

    private String type;

    NotificationType(String type) {
        this.type = type;
    }
}
