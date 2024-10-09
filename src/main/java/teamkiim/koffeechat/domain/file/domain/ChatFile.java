package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
