package teamkiim.koffeechat.chat.domain.message;

import lombok.Getter;

@Getter
public enum MessageType {

    ENTER("입장"),
    EXIT("퇴장"),
    CHAT("채팅"),
    IMAGE("이미지");

    private String name;

    MessageType(String name) {
        this.name = name;
    }
}
