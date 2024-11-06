package teamkiim.koffeechat.domain.chat.room.common.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.member.domain.Member;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE mcr.member = :member AND mcr.active = true")
    List<MemberChatRoom> findAllByMember(@Param("member") Member member);

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE mcr.member = :member AND mcr.chatRoom = :chatRoom AND mcr.active = true")
    Optional<MemberChatRoom> findByMemberAndActiveChatRoom(@Param("member") Member member,
                                                           @Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE mcr.member = :member AND mcr.chatRoom = :chatRoom AND mcr.active = false")
    Optional<MemberChatRoom> findByMemberAndNotActiveChatRoom(@Param("member") Member member,
                                                              @Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE mcr.chatRoom = :chatRoom AND mcr.member <> :member")
    Optional<MemberChatRoom> findByChatRoomExceptMember(@Param("chatRoom") ChatRoom chatRoom,
                                                        @Param("member") Member member);

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE mcr.chatRoom = :chatRoom")
    List<MemberChatRoom> findAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT mcr FROM MemberChatRoom mcr WHERE " +
            "mcr.member =:member AND " +
            "(:chatRoomType IS NULL " +
            "OR mcr.chatRoom.chatRoomType =:chatRoomType) " +
            "AND mcr.active = true")
    Page<MemberChatRoom> findAllByMemberAndChatRoomType(@Param("member") Member member,
                                                        @Param("chatRoomType") ChatRoomType chatRoomType,
                                                        Pageable pageable);

}
