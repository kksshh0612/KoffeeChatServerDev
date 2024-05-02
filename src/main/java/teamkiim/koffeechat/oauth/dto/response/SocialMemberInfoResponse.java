package teamkiim.koffeechat.oauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialMemberInfoResponse {

    private String email;
    private String nickname;
}
