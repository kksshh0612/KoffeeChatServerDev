package teamkiim.koffeechat.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.chat.domain.room.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
