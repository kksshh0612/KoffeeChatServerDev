package teamkiim.koffeechat.domain.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveSocialLoginMemberInfoServiceRequest {

    private String email;
    private String nickname;
}
