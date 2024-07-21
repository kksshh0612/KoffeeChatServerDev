package teamkiim.koffeechat.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import teamkiim.koffeechat.chat.domain.message.MessageType;
import teamkiim.koffeechat.chat.controller.dto.ChatMessageRequest;
import teamkiim.koffeechat.chat.service.ChatMessageService;
import teamkiim.koffeechat.chat.service.ChatRoomService;

import java.time.LocalDateTime;

@Controller         // HTTP 가 아니기 때문에 @RestController 사용 X
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void enter(ChatMessageRequest chatMessageRequest){

        LocalDateTime createdTime = LocalDateTime.now();

        if(chatMessageRequest.getMessageType().equals(MessageType.ENTER)){

            chatRoomService.joinChatRoom(chatMessageRequest.getChatRoomId(), chatMessageRequest.getSenderId());
            chatMessageRequest.setContent(chatMessageRequest.getSenderNickname() + "님이 입장했습니다.");
        }
        chatMessageService.save(chatMessageRequest.toServiceRequest(createdTime));

        messagingTemplate.convertAndSend("/sub/chat/" + chatMessageRequest.getChatRoomId(), chatMessageRequest);
    }


}
