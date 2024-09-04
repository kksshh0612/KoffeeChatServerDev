package teamkiim.koffeechat.domain.chat.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    @MessageMapping("/chat/{chatRoomId}")
    public void sendChatMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessageRequest chatMessageRequest,
                                WebSocketSession session){

        Long memberId = (Long)session.getAttributes().get("memberId");

        LocalDateTime createdTime = LocalDateTime.now();

        System.out.println("채팅방 id : " + chatRoomId);
        System.out.println(chatMessageRequest.getContent());

        chatMessageService.save(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
        chatMessageService.send(chatMessageRequest.toServiceRequest(createdTime), chatRoomId, memberId);
    }


}
