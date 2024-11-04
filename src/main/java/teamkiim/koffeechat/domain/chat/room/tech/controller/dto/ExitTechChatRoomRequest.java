package teamkiim.koffeechat.domain.chat.room.tech.controller.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.ExitTechChatRoomServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExitTechChatRoomRequest {

    private String chatRoomId;

    public ExitTechChatRoomServiceRequest toServiceRequest(Long chatRoomId, LocalDateTime currTime) {
        return ExitTechChatRoomServiceRequest.builder()
                .chatRoomId(chatRoomId)
                .exitTime(currTime)
                .build();
    }
}
