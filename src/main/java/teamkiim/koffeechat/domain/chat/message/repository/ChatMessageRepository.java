package teamkiim.koffeechat.domain.chat.message.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findAllByChatRoomId(Long chatRoomId, Pageable pageable);

    @Query(value = "{ 'chatRoomId': chatRoomId, 'createdTime': { $gt: lastReadTime } }", count = true)
    long findCountByChatRoomId(Long chatRoomId, LocalDateTime lastReadTime);
}
