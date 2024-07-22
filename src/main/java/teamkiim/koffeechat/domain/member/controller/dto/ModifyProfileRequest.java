package teamkiim.koffeechat.domain.member.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.dto.request.ModifyProfileServiceRequest;
import teamkiim.koffeechat.domain.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyProfileRequest {

    @NotBlank(message = "닉네임 입력은 필수입니다")
    private String nickname;
    @NotNull(message = "회원 직업 입력은 필수입니다")
    private MemberRole memberRole;

    public ModifyProfileServiceRequest toServiceRequest(){
        return ModifyProfileServiceRequest.builder()
                .nickname(this.nickname)
                .memberRole(this.memberRole)
                .build();
    }
}
