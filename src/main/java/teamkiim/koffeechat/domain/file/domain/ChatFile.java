package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DiscriminatorValue("CHAT")
@NoArgsConstructor
public class ChatFile extends File {

    @Builder
    public ChatFile(String url) {
        super(url);
    }
}
