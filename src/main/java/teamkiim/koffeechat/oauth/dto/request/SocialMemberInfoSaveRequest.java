package teamkiim.koffeechat.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialMemberInfoSaveRequest {

    private String email;
    private String nickname;
}
