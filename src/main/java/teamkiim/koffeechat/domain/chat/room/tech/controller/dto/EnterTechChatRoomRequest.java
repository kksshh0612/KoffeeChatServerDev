package teamkiim.koffeechat.domain.chat.room.tech.controller.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.EnterTechChatRoomServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnterTechChatRoomRequest {

    private String chatRoomId;

    public EnterTechChatRoomServiceRequest toServiceRequest(LocalDateTime currTime, Long chatRoomId) {
        return EnterTechChatRoomServiceRequest.builder()
                .chatRoomId(chatRoomId)
                .enterTime(currTime)
                .build();
    }
}
