package teamkiim.koffeechat.domain.chat.message.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 커서 기반 페이징 조회
    @Query(value = "{ 'chatRoomId': ?0, 'seqId': { $lt: ?1 } }", sort = "{ 'seqId': -1 }")
    Page<ChatMessage> findByCursor(Long chatRoomId, Long cursorId, Pageable pageable);

    // 가장 최신의 메세지 페이징 조회
    @Query(value = "{ 'chatRoomId': ?0}", sort = "{ 'seqId': -1 }")
    Page<ChatMessage> findLatestMessage(Long chatRoomId, Pageable pageable);

    // 안읽은 메세지 수 카운팅
    @Query(value = "{ 'chatRoomId': ?0, 'createdTime': { $gt: ?1 } }", count = true)
    long findCountByChatRoomId(Long chatRoomId, LocalDateTime lastReadTime);
}
