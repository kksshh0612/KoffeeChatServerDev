package teamkiim.koffeechat.domain.chat.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import teamkiim.koffeechat.domain.chat.message.controller.dto.ChatMessageRequest;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.chat.room.common.ChatRoomManager;
import teamkiim.koffeechat.domain.chat.room.common.service.ChatRoomService;
import teamkiim.koffeechat.domain.notification.service.ChatNotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Controller         // HTTP 가 아니기 때문에 @RestController 사용 X
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatNotificationService chatNotificationService;
    private final ChatRoomManager chatRoomManager;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/text/{chatRoomId}")
    public void sendTextMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessageRequest chatMessageRequest,
                                SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Long senderId = (Long) simpMessageHeaderAccessor.getSessionAttributes().get("memberId");

        LocalDateTime createdTime = LocalDateTime.now();

        List<Long> chatRoomMemberIds = chatRoomManager.getMemberIds(chatRoomId);

        ChatMessageServiceRequest chatMessageServiceRequest = chatMessageService.saveTextMessage(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, senderId);
        chatMessageService.send(chatMessageServiceRequest, chatRoomId, senderId);
        chatNotificationService.createChatNotification(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, senderId, chatRoomMemberIds);
    }

    @MessageMapping("/chat/source-code/{chatRoomId}")
    public void sendSourceCodeMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessageRequest chatMessageRequest,
                                      SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Long senderId = (Long) simpMessageHeaderAccessor.getSessionAttributes().get("memberId");

        LocalDateTime createdTime = LocalDateTime.now();

        List<Long> chatRoomMemberIds = chatRoomManager.getMemberIds(chatRoomId);

        chatMessageService.saveSourceCodeMessage(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, senderId);
        chatMessageService.send(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, senderId);
        chatNotificationService.createChatNotification(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, senderId, chatRoomMemberIds);
    }
}
