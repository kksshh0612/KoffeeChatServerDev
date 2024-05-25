package teamkiim.koffeechat.oauth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.oauth.dto.request.SaveSocialLoginMemberInfoServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveSocialLoginMemberInfoRequest {

    private String email;
    private String nickname;

    public SaveSocialLoginMemberInfoServiceRequest toServiceRequest(){
        return SaveSocialLoginMemberInfoServiceRequest.builder()
                .email(this.email)
                .nickname(this.nickname)
                .build();
    }
}
