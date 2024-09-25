package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

public class ChatFile extends File {

    @Builder
    public ChatFile(Long chatRoomId) {
        super("CHAT", UUID.randomUUID() + "_" + chatRoomId);
    }
}
