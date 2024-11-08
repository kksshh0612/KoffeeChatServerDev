package teamkiim.koffeechat.domain.chat.room.common.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatNotificationService chatNotificationService;

    /**
     * 채팅방 닫기
     *
     * @param chatRoomId 채팅방 PK
     * @param memberId   채팅방 닫은 회원 PK
     * @param closeTime  채팅방 닫은 시간
     */
    @Transactional
    public void close(Long chatRoomId, Long memberId, LocalDateTime closeTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndActiveChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        memberChatRoom.updateCloseTime(closeTime);

        // 채팅방 알림 on
        chatNotificationService.onChatRoomNotification(memberId, chatRoomId);

        log.info("[ChatRoomService / close] chatRoomId : {}, memberId : {}", chatRoomId, memberId);
    }

    public void closeChatRoomList(Long memberId) {

        chatNotificationService.stopNotifications(memberId);
    }
}
