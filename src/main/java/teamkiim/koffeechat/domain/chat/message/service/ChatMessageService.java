package teamkiim.koffeechat.domain.chat.message.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.dto.response.ChatMessageResponse;
import teamkiim.koffeechat.domain.chat.message.repository.ChatMessageRepository;
import teamkiim.koffeechat.domain.chat.room.common.domain.ChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.chat.room.common.repository.ChatRoomRepository;
import teamkiim.koffeechat.domain.chat.room.common.repository.MemberChatRoomRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;

    private final AESCipherUtil aesCipherUtil;
    private final ChatNotificationService chatNotificationService;

    /**
     * 텍스트 채팅 메세지 저장 & 전송
     *
     * @param messageRequest    채팅 DTO
     * @param decryptChatRoomId 복호화된 채팅방 PK
     * @param encryptChatRoomId 암호화된 채팅방 PK
     * @param senderId          채팅 전송한 회원 PK
     */
    @Transactional
    public void saveTextMessage(ChatMessageServiceRequest messageRequest,
                                Long decryptChatRoomId, String encryptChatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, decryptChatRoomId, senderId);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatRoom chatRoom = chatRoomRepository.findById(decryptChatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        chatRoom.updateLastMessageTime(messageRequest.getCreatedTime());            // 가장 최근 메세지 전송 시간 업데이트

        messageRequest.setMessageId(savedMessage.getId());

        send(chatMessage, encryptChatRoomId, senderId);
    }

    /**
     * 소스코드 채팅 메세지 저장 & 전송
     *
     * @param messageRequest    채팅 DTO
     * @param decryptChatRoomId 복호화된 채팅방 PK
     * @param encryptChatRoomId 암호화된 채팅방 PK
     * @param senderId          채팅 전송한 회원 PK
     */
    public void saveSourceCodeMessage(ChatMessageServiceRequest messageRequest,
                                      Long decryptChatRoomId, String encryptChatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, decryptChatRoomId, senderId);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatRoom chatRoom = chatRoomRepository.findById(decryptChatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        chatRoom.updateLastMessageTime(messageRequest.getCreatedTime());            // 가장 최근 메세지 전송 시간 업데이트

        messageRequest.setMessageId(savedMessage.getId());

        send(chatMessage, encryptChatRoomId, senderId);
    }

    /**
     * 이미지 채팅 메세지 저장 & 전송
     *
     * @param messageRequest    채팅 DTO
     * @param decryptChatRoomId 복호화된 채팅방 PK
     * @param encryptChatRoomId 암호화된 채팅방 PK
     * @param senderId          채팅 전송한 회원 PK
     */
    public void saveImageMessage(ChatMessageServiceRequest messageRequest,
                                 Long decryptChatRoomId, String encryptChatRoomId, Long senderId) {

        Long seqId = sequenceGeneratorService.generateSequence("chat_message_seq");         // 순차 id 부여

        ChatMessage chatMessage = messageRequest.toEntity(seqId, decryptChatRoomId, senderId);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatRoom chatRoom = chatRoomRepository.findById(decryptChatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        chatRoom.updateLastMessageTime(messageRequest.getCreatedTime());            // 가장 최근 메세지 전송 시간 업데이트

        messageRequest.setMessageId(savedMessage.getId());

        send(chatMessage, encryptChatRoomId, senderId);
    }

    /**
     * subscribers에게 채팅 메세지 전송
     *
     * @param chatMessage 전송 메세지
     * @param chatRoomId  전송할 채팅방 PK
     * @param senderId    전송한 회원 PK
     */
    private void send(ChatMessage chatMessage, String chatRoomId, Long senderId) {

        Member member = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.of(chatMessage,
                aesCipherUtil.encrypt(member.getId()), member);

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatMessageResponse);
    }

    /**
     * 채팅 메세지 커서 기반 페이징 조회
     *
     * @param chatRoomId 메세지 조회할 채팅방 PK
     * @return List<ChatMessageResponse>
     */
    @Transactional
    public List<ChatMessageResponse> getChatMessages(Long chatRoomId, Long cursorId, int size, Long memberId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByChatRoom(chatRoom);

        // 채팅방에 참여한 멤버들
        List<Member> joinMemberList = memberChatRoomList.stream()
                .map(MemberChatRoom::getMember)
                .toList();

        // 처음 조회할 경우 cursorId == null
        if (cursorId == null) {
            cursorId = Long.MAX_VALUE;
        }
        // cursor 기반 페이징 조회
        PageRequest pageRequest = PageRequest.of(0, size);
        List<ChatMessage> messageList = chatMessageRepository.findByCursor(chatRoomId, cursorId, pageRequest)
                .getContent();

        List<ChatMessageResponse> chatMessageResponseList = messageList.stream()
                .map(chatMessage -> ChatMessageResponse.of(chatMessage, joinMemberList,
                        aesCipherUtil.encrypt(chatMessage.getSenderId()), memberId))
                .toList();

        chatNotificationService.offChatRoomNotification(memberId, chatRoomId);

        return chatMessageResponseList;
    }

    /**
     * 회원별 채팅방 정보 조회 (채팅방 목록 조회 시 호출)
     *
     * @param memberChatRoomList 회원이 현재 참여중인 채팅방 리스트
     * @return List<ChatRoomInfoDto>
     */
    public List<ChatRoomInfoDto> getChatRoomInfo(List<MemberChatRoom> memberChatRoomList) {

        List<ChatRoomInfoDto> dtoList = new ArrayList<>();

        for (MemberChatRoom memberChatRoom : memberChatRoomList) {
            PageRequest pageRequest = PageRequest.of(0, 1);     // 하나만 조회

            List<ChatMessage> latestMessage = chatMessageRepository.findLatestByChatRoom(
                    memberChatRoom.getChatRoom().getId(), pageRequest).getContent();

            String latestMessageContent = latestMessage.stream()
                    .findFirst()
                    .map(ChatMessage::getContent)
                    .orElse(null);

            LocalDateTime latestMessageTime = latestMessage.stream()
                    .findFirst()
                    .map(ChatMessage::getCreatedTime)
                    .orElse(null);

            long unreadMessageCount = chatMessageRepository.findCountByChatRoomId(memberChatRoom.getChatRoom().getId(),
                    memberChatRoom.getCloseTime());

            dtoList.add(
                    new ChatRoomInfoDto(memberChatRoom, latestMessageContent, latestMessageTime, unreadMessageCount));

            log.info("[ChatMessageService / getChatRoomInfo] 채팅방 id : {} / latestMessageTime : {}",
                    memberChatRoom.getChatRoom().getId(), latestMessageTime);
        }

        return dtoList;
    }

}
