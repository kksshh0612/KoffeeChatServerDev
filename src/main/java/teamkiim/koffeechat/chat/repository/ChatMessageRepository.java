package teamkiim.koffeechat.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import teamkiim.koffeechat.chat.domain.message.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
