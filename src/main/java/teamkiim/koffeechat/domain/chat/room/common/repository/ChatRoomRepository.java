package teamkiim.koffeechat.domain.chat.room.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findById(Long id);
}
