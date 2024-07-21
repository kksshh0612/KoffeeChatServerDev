package teamkiim.koffeechat.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.chat.domain.room.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


}
