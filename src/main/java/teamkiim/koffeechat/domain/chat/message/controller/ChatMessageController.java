package teamkiim.koffeechat.domain.chat.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import teamkiim.koffeechat.domain.chat.message.controller.dto.ChatMessageRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;

@Controller         // HTTP 가 아니기 때문에 @RestController 사용 X
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/text/{chatRoomId}")
    public void sendTextMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessageRequest chatMessageRequest,
                                SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Long memberId = (Long) simpMessageHeaderAccessor.getSessionAttributes().get("memberId");

        LocalDateTime createdTime = LocalDateTime.now();

        chatMessageService.saveTextMessage(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
        chatMessageService.send(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
    }

    @MessageMapping("/chat/source-code/{chatRoomId}")
    public void sendSourceCodeMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessageRequest chatMessageRequest,
                                      SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Long memberId = (Long) simpMessageHeaderAccessor.getSessionAttributes().get("memberId");

        LocalDateTime createdTime = LocalDateTime.now();

        chatMessageService.saveSourceCodeMessage(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
        chatMessageService.send(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
    }

}
