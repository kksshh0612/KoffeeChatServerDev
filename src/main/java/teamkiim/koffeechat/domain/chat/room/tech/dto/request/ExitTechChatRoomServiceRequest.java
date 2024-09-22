package teamkiim.koffeechat.domain.chat.room.tech.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExitTechChatRoomServiceRequest {

    private Long chatRoomId;
    private LocalDateTime exitTime;
}
