package teamkiim.koffeechat.domain.chat.message.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {


    // 커서 기반 페이징 조회
    @Query(value = "{ 'chatRoomId': chatRoomId, 'seqId': { $lt: cursorId } }", sort = "{ 'seqId': -1 }")
    Page<ChatMessage> findByCursor(Long chatRoomId, Long cursorId, Pageable pageable);

    // 가장 최신의 메세지 페이징 조회
    @Query(value = "{ 'chatRoomId': chatRoomId}", sort = "{ 'seqId': -1 }")
    Page<ChatMessage> findLatestMessage(Long chatRoomId, Pageable pageable);

    @Query(value = "{ 'chatRoomId': chatRoomId, 'createdTime': { $gt: lastReadTime } }", count = true)
    long findCountByChatRoomId(Long chatRoomId, LocalDateTime lastReadTime);
}
