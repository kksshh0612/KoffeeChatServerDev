package teamkiim.koffeechat.domain.chat.room.tech.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.ExitTechChatRoomServiceRequest;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExitTechChatRoomRequest {

    private Long chatRoomId;

    public ExitTechChatRoomServiceRequest toServiceRequest(LocalDateTime currTime){
        return ExitTechChatRoomServiceRequest.builder()
                .chatRoomId(chatRoomId)
                .exitTime(currTime)
                .build();
    }
}
