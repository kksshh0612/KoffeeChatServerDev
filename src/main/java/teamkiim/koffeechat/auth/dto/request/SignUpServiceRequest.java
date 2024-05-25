package teamkiim.koffeechat.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpServiceRequest {

    private String email;
    private String password;
    private String nickname;
    private MemberRole memberRole;

    public Member toEntity(){
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .memberRole(this.memberRole)
                .build();

    }
}
