package teamkiim.koffeechat.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import teamkiim.koffeechat.domain.chat.domain.message.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
