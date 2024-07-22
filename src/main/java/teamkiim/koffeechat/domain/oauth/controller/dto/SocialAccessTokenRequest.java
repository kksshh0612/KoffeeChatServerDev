package teamkiim.koffeechat.domain.oauth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.oauth.SocialLoginType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccessTokenRequest {

    private String accessToken;
    private SocialLoginType socialLoginType;

}
