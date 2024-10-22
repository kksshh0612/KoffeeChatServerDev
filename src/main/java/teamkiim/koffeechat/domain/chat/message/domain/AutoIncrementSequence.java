package teamkiim.koffeechat.domain.chat.message.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auto_increment_sequence")
@Getter
@NoArgsConstructor
public class AutoIncrementSequence {

    @Id
    private String id;                      // mongoDB의 고유 식별자 ObjectId 사용

    private Long seq;

}
