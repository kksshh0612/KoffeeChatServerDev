package teamkiim.koffeechat.domain.chat.message.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.controller.dto.ChatMessageResponse;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.repository.ChatMessageRepository;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 채팅 메세지 저장
     *
     * @param messageRequest
     * @param chatRoomId
     * @param senderId
     */
    @Transactional
    public void saveTextMessage(ChatMessageServiceRequest messageRequest, Long chatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, chatRoomId, senderId);

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        chatRoom.updateLastMessageTime(messageRequest.getCreatedTime());            // 가장 최근 메세지 전송 시간 업데이트

        messageRequest.setMessageId(savedMessage.getId());

        send(chatMessage, chatRoomId, senderId);
    }

    public void saveSourceCodeMessage(ChatMessageServiceRequest messageRequest, Long chatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, chatRoomId, senderId);
        chatMessageRepository.save(chatMessage);

        send(chatMessage, chatRoomId, senderId);
    }

    public void saveImageMessage(ChatMessageServiceRequest messageRequest, Long chatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, chatRoomId, senderId);
        chatMessageRepository.save(chatMessage);

        send(chatMessage, chatRoomId, senderId);
    }

    /**
     * subscribers에게 채팅 메세지 전송
     *
     * @param chatMessage
     * @param chatRoomId
     * @param senderId
     */
    public void send(ChatMessage chatMessage, Long chatRoomId, Long senderId) {

        Member member = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.of(chatMessage,
                aesCipherUtil.encrypt(member.getId()), member);

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatMessageResponse);
    }

    /**
     * 채팅 메세지 커서 기반 페이징 조회
     *
     * @param chatRoomId
     * @return List<ChatMessageResponse>
     */
    @Transactional
    public List<ChatMessageResponse> getChatMessages(Long chatRoomId, Long cursorId, int size, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByChatRoom(chatRoom);

        // 채팅방에 참여한 멤버들
        List<Member> joinMemberList = memberChatRoomList.stream()
                .map(MemberChatRoom::getMember)
                .toList();

        PageRequest pageRequest = PageRequest.of(0, size);

        // cursor 기반 페이징
        List<ChatMessage> messageList = chatMessageRepository.findByCursor(chatRoomId, cursorId, pageRequest)
                .getContent();

        List<ChatMessageResponse> chatMessageResponseList = messageList.stream()
                .map(chatMessage -> teamkiim.koffeechat.domain.chat.message.dto.response.ChatMessageResponse.of(
                        chatMessage, joinMemberList, memberId))
                .toList();

        return chatMessageResponseList;
    }

    public List<ChatRoomInfoDto> countUnreadMessageCount(List<MemberChatRoom> memberChatRoomList) {

        List<ChatRoomInfoDto> dtoList = new ArrayList<>();

        for (MemberChatRoom memberChatRoom : memberChatRoomList) {
            long unreadMessageCount = chatMessageRepository.findCountByChatRoomId(memberChatRoom.getChatRoom().getId(),
                    memberChatRoom.getCloseTime());

            dtoList.add(new ChatRoomInfoDto(memberChatRoom, unreadMessageCount));
        }

        return dtoList;
    }

}
