package teamkiim.koffeechat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.chat.domain.room.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


}
