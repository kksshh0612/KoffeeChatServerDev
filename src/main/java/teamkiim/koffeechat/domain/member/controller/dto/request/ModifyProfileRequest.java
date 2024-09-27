package teamkiim.koffeechat.domain.member.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.dto.request.ModifyProfileServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 프로필 수정 Request")
public class ModifyProfileRequest {

    @Schema(description = "회원 닉네임 수정", example = "커피챗수정")
    @NotBlank(message = "닉네임 입력은 필수입니다")
    private String nickname;

    @Schema(description = "회원 직업 수정")
    @NotNull(message = "회원 직업 입력은 필수입니다")
    private MemberRole memberRole;

    public ModifyProfileServiceRequest toServiceRequest() {
        return ModifyProfileServiceRequest.builder()
                .nickname(this.nickname)
                .memberRole(this.memberRole)
                .build();
    }
}
