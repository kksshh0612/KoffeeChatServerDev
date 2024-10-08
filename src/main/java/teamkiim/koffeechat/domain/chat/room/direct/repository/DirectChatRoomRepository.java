package teamkiim.koffeechat.domain.chat.room.direct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.chat.room.direct.domain.DirectChatRoom;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.Optional;

public interface DirectChatRoomRepository extends JpaRepository<DirectChatRoom, Long> {

    @Query("select dcr from DirectChatRoom dcr " +
            "join MemberChatRoom mcr1 on dcr.id = mcr1.chatRoom.id " +
            "join MemberChatRoom mcr2 on dcr.id = mcr2.chatRoom.id " +
            "where mcr1.member = :member1 and mcr2.member = :member2")
    Optional<DirectChatRoom> findDirectChatRoomByMembers(@Param("member1") Member member1,
                                                         @Param("member2") Member member2);
}
