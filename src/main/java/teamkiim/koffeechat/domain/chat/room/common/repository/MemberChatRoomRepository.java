package teamkiim.koffeechat.domain.chat.room.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    List<MemberChatRoom> findAllByMember(Member member);

    Optional<MemberChatRoom> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    Page<MemberChatRoom> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);

    Page<MemberChatRoom> findAllByMember(Member member, Pageable pageable);
}
