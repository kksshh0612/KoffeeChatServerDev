package teamkiim.koffeechat.domain.chat.room.tech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;

public interface TechChatRoomRepository extends JpaRepository<TechChatRoom, Long> {


}
