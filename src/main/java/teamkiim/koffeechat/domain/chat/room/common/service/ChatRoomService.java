package teamkiim.koffeechat.domain.chat.room.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.ChatRoomManager;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.common.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final ChatRoomManager chatRoomManager;
    private final ChatNotificationService chatNotificationService;

    /**
     * 참여중인 채팅방 목록 조회
     * -> 채팅방 별 사용자의 퇴장 시간 기준 안읽은 메세지 수, 마지막 메세지 리턴
     *
     * @param memberId
     * @param page
     * @param size
     * @param chatRoomType
     * @return
     */
    public List<ChatRoomListResponse> findChatRoomList(Long memberId, int page, int size, ChatRoomType chatRoomType) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        chatNotificationService.startNotifications(memberId);

//        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Order.desc("lastMessageTime").nullsLast()));      // 커서 기반이기 때문에 page 설정 안하기 위함.

        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<MemberChatRoom> memberChatRoomList =
                memberChatRoomRepository.findAllByMemberAndChatRoomType(member, chatRoomType, pageRequest).getContent();

//        List<MemberChatRoom> memberChatRoomList =
//                memberChatRoomRepository.findAllByMemberAndChatRoomType(member, chatRoomType, cursorId, pageRequest).getContent();

        List<ChatRoomInfoDto> chatRoomInfoDtoList = chatMessageService.countUnreadMessageCount(memberChatRoomList);

        List<ChatRoomListResponse> chatRoomListResponseList = new ArrayList<>();

        for (ChatRoomInfoDto chatRoomInfoDto : chatRoomInfoDtoList) {
            MemberChatRoom memberChatRoom = chatRoomInfoDto.getMemberChatRoom();

            MemberChatRoom oppositeMemberChatRoom = memberChatRoomRepository.findByChatRoomExceptMember(memberChatRoom.getChatRoom(), memberChatRoom.getMember())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

            ChatRoomListResponse chatRoomListResponse = ChatRoomListResponse.of(chatRoomInfoDto, oppositeMemberChatRoom.getMember());

            chatRoomListResponseList.add(chatRoomListResponse);
        }

        chatNotificationService.startNotifications(memberId);

        return chatRoomListResponseList;
    }

    /**
     * 참여중인 채팅방 chatRoomId (PK) 로 단건 조회
     * @param chatRoomId
     * @param memberId
     * @return
     */
//    public ChatRoomListResponse findChatRoom(Long chatRoomId, Long memberId){
//
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
//
//        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
//                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
//
//        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));
//
//        long unreadMessageCount = chatMessageService.countUnreadMessageCount(memberChatRoom);
//
//        ChatRoomInfoDto chatRoomInfoDto = new ChatRoomInfoDto(memberChatRoom, unreadMessageCount);
//
//        return ChatRoomListResponse.of(chatRoomInfoDto);
//    }


    /**
     * 채팅방 닫기
     *
     * @param chatRoomId
     * @param memberId
     * @param closeTime
     */
    @Transactional
    public void close(Long chatRoomId, Long memberId, LocalDateTime closeTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        memberChatRoom.updateCloseTime(closeTime);

        // 채팅방 알림 on
        chatNotificationService.onChatRoomNotification(memberId, chatRoomId);
    }

    /**
     * 채팅방 퇴장
     *
     * @param chatRoomId
     * @param memberId
     * @param exitTime
     */
    @Transactional
    public void exit(Long chatRoomId, Long memberId, LocalDateTime exitTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        // 퇴장 메세지 전송
        ChatMessageServiceRequest chatMessageServiceRequest = ChatMessageServiceRequest.builder()
                .messageType(MessageType.EXIT)
                .content(member.getNickname() + " 님이 퇴장하셨습니다")
                .createdTime(exitTime)
                .build();

        chatMessageService.saveTextMessage(chatMessageServiceRequest, chatRoomId, memberId);
        chatMessageService.send(chatMessageServiceRequest, chatRoomId, memberId);

        // memberChatRoom 삭제
        memberChatRoomRepository.delete(memberChatRoom);

        chatRoomManager.removeMember(chatRoomId, member);

        // 채팅 알림 정보 삭제
        chatNotificationService.removeChatRoomNotification(memberId, chatRoomId);
    }

    public String findChatRoomName(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        return chatRoom.getName();
    }
}
