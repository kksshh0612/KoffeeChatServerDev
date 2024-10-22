package teamkiim.koffeechat.domain.chat.room.direct.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoomType;
import teamkiim.koffeechat.domain.chat.room.common.ChatRoomManager;
import teamkiim.koffeechat.domain.chat.room.direct.domain.DirectChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.message.dto.response.ChatMessageResponse;
import teamkiim.koffeechat.domain.chat.message.repository.ChatMessageRepository;
import teamkiim.koffeechat.domain.chat.room.direct.dto.response.CreateDirectChatRoomResponse;
import teamkiim.koffeechat.domain.chat.room.direct.repository.DirectChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DirectChatRoomService {

    private final DirectChatRoomRepository directChatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomManager chatRoomManager;
    private final ChatNotificationService chatNotificationService;

    /**
     * 일대일 채팅방 생성
     * @param memberId1
     * @param memberId2
     * @return
     */
    @Transactional
    public CreateDirectChatRoomResponse createChatRoom(Long memberId1, Long memberId2){

        Member member1 = memberRepository.findById(memberId1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member member2 = memberRepository.findById(memberId2)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<DirectChatRoom> existChatRoom = directChatRoomRepository.findDirectChatRoomByMembers(member1, member2);

        if(existChatRoom.isPresent()){
            return new CreateDirectChatRoomResponse(existChatRoom.get().getId());
        }

        DirectChatRoom directChatRoom = DirectChatRoom.builder()
                .chatRoomType(ChatRoomType.DIRECT)
                .name("DIRECT MESSAGE")
                .lastMessageTime(null)
                .build();

        DirectChatRoom saveChatRoom = directChatRoomRepository.save(directChatRoom);

        MemberChatRoom memberChatRoom1 = MemberChatRoom.builder()
                .member(member1)
                .chatRoom(directChatRoom)
                .roomName(member2.getNickname())
                .build();

        MemberChatRoom memberChatRoom2 = MemberChatRoom.builder()
                .chatRoom(directChatRoom)
                .member(member2)
                .roomName(member1.getNickname())
                .build();

        memberChatRoomRepository.saveAll(List.of(memberChatRoom1, memberChatRoom2));

        // 채팅방 멤버 관리 추가
        chatRoomManager.addMember(saveChatRoom.getId(), member1);
        chatRoomManager.addMember(saveChatRoom.getId(), member2);

        // 채팅 알림 등록
        chatNotificationService.addChatRoomNotification(memberId1, saveChatRoom.getId());
        chatNotificationService.addChatRoomNotification(memberId2, saveChatRoom.getId());

        return new CreateDirectChatRoomResponse(saveChatRoom.getId());
    }

    /**
     * 일대일 채팅방 오픈 (존재하면 채팅 데이터 조회 / 존재하지 않으면 생성)
     * @param chatRoomId
     * @return List<ChatMessageResponse>
     */
    @Transactional
    public List<ChatMessageResponse> openChatRoom(Long chatRoomId, int size, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        DirectChatRoom directChatRoom = directChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByChatRoom(directChatRoom);

        // 채팅방에 참여한 멤버들
        List<Member> joinMemberList = memberChatRoomList.stream()
                .map(MemberChatRoom::getMember)
                .toList();

        PageRequest pageRequest = PageRequest.of(0, size);

        // cursor 기반 페이징
        List<ChatMessage> messageList = chatMessageRepository.findLatestMessage(chatRoomId, pageRequest).getContent();

        List<ChatMessageResponse> chatMessageResponseList = messageList.stream()
                .map(chatMessage -> ChatMessageResponse.of(chatMessage, joinMemberList, memberId))
                .toList();

        // 채팅 알림 off
        chatNotificationService.offChatRoomNotification(member.getId(), directChatRoom.getId());

        return chatMessageResponseList;
    }

    @Transactional
    public void closeChatRoom(Long chatRoomId, Long memberId, LocalDateTime closeTime){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = directChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CHAT_ROOM_NOT_FOUND));

        memberChatRoom.updateCloseTime(closeTime);
    }

}
