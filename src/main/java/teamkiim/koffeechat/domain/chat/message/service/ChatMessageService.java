package teamkiim.koffeechat.domain.chat.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;
import teamkiim.koffeechat.domain.chat.message.controller.dto.ChatMessageResponse;
import teamkiim.koffeechat.domain.chat.message.domain.ChatMessage;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.repository.ChatMessageRepository;
import teamkiim.koffeechat.domain.chat.room.common.domain.MemberChatRoom;
import teamkiim.koffeechat.domain.chat.room.common.dto.ChatRoomInfoDto;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 메세지 저장
     * @param messageRequest
     * @param chatRoomId
     * @param senderId
     */
    public void save(ChatMessageServiceRequest messageRequest, Long chatRoomId, Long senderId) {

        ChatMessage chatMessage = messageRequest.toEntity(chatRoomId, senderId);
        chatMessageRepository.save(chatMessage);
    }

    /**
     * subscribers에게 채팅 메세지 전송
     * @param messageRequest
     * @param chatRoomId
     * @param senderId
     */
    public void send(ChatMessageServiceRequest messageRequest, Long chatRoomId, Long senderId) {

        Member member = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.of(messageRequest, member, chatRoomId);

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatMessageResponse);
    }

    public List<ChatRoomInfoDto> findCount(List<MemberChatRoom> memberChatRoomList) {

        List<ChatRoomInfoDto> dtoList = new ArrayList<>();

        for(MemberChatRoom memberChatRoom : memberChatRoomList) {
            long unreadMessageCount = chatMessageRepository.findCountByChatRoomId(memberChatRoom.getChatRoom().getId(), memberChatRoom.getCloseTime());

            dtoList.add(new ChatRoomInfoDto(memberChatRoom, unreadMessageCount));
        }

        return dtoList;
    }
}
