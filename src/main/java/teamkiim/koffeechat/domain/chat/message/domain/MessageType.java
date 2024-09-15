package teamkiim.koffeechat.domain.chat.message.domain;

import lombok.Getter;

@Getter
public enum MessageType {

    ENTER("입장"),
    EXIT("퇴장"),
    CHAT("채팅"),
    SOURCE_CODE("소스코드"),
    IMAGE("이미지");

    private String name;

    MessageType(String name) {
        this.name = name;
    }
}
