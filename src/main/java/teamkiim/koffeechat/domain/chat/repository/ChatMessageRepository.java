package teamkiim.koffeechat.domain.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import teamkiim.koffeechat.domain.chat.domain.message.ChatMessage;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
}
