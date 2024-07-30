package teamkiim.koffeechat.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.domain.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
    @NotBlank(message = "닉네임을 입력해주세요")
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
