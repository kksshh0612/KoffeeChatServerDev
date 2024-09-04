package teamkiim.koffeechat.domain.chat.message.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findAllByChatRoomId(Long chatRoomId);

    @Query(value = "{ 'chatRoomId': chatRoomId, 'createdTime': { $gt: lastReadTime } }", count = true)
    long findCountByChatRoomId(Long chatRoomId, LocalDateTime lastReadTime);
}
