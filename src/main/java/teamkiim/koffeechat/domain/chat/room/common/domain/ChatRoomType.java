package teamkiim.koffeechat.domain.chat.room.common.domain;

import lombok.Getter;

@Getter
public enum ChatRoomType {

    TECH("기술"),
    DIRECT("다이렉트");

    private String name;

    ChatRoomType(String name) {
        this.name = name;
    }
}
