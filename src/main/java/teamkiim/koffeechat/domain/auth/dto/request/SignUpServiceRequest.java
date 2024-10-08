package teamkiim.koffeechat.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpServiceRequest {

    private String email;
    private String password;
    private String nickname;
    private MemberRole memberRole;

    public Member toEntity(String basicProfileImageUrl){
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .memberRole(this.memberRole)
                .followerCount(0L)
                .followingCount(0L)
                .profileImageUrl(basicProfileImageUrl)
                .build();

    }
}
