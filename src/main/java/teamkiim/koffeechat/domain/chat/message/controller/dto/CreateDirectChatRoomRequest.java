package teamkiim.koffeechat.domain.chat.message.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectChatRoomRequest {

    private Long memberId;
}
