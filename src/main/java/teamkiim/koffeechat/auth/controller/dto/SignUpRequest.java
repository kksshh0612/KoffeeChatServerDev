package teamkiim.koffeechat.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String nickname;
    private MemberRole memberRole;

    public SignUpServiceRequest toServiceRequest(){
        return SignUpServiceRequest.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .memberRole(this.memberRole)
                .build();

    }
}
