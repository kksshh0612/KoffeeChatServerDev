package teamkiim.koffeechat.domain.memberfollow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAndLoginMemberDto {

    Member member;
    Member loginMember;

    public static MemberAndLoginMemberDto of(Member member, Member loginMember) {
        return MemberAndLoginMemberDto.builder()
                .member(member)
                .loginMember(loginMember)
                .build();
    }
}
