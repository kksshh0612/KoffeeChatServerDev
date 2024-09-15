package teamkiim.koffeechat.domain.chat.room.tech.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.EnterTechChatRoomServiceRequest;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnterTechChatRoomRequest {

    private Long chatRoomId;

    public EnterTechChatRoomServiceRequest toServiceRequest(LocalDateTime currTime) {
        return EnterTechChatRoomServiceRequest.builder()
                .chatRoomId(chatRoomId)
                .enterTime(currTime)
                .build();
    }
}
