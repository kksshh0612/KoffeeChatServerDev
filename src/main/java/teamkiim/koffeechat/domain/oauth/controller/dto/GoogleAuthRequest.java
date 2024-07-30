package teamkiim.koffeechat.domain.oauth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.oauth.dto.request.GoogleAuthServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {

    private String clientId;
    private String redirectUri;
    private String code;
    private String clientSecret;

    public GoogleAuthServiceRequest toServiceRequest(){
        return GoogleAuthServiceRequest.builder()
                .clientId(this.clientId)
                .redirectUri(this.redirectUri)
                .code(this.code)
                .clientSecret(this.clientSecret)
                .build();
    }
}
