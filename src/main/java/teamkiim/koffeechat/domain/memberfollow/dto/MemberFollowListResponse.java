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
public class MemberFollowListResponse {

    private String memberId;
    private String email;
    private String nickname;
    private String profileImagePath;
    private String profileImageName;
    private boolean isFollowedByLoginMember;                            //로그인된 사용자가 팔로우하는 회원
    private boolean isLoginMember;                                      //팔로우 목록에 로그인된 사용자가 포함된 경우

    public static MemberFollowListResponse of(String memberId, Member member, boolean isFollowedByLoginMember, boolean isLoginMember) {

        return MemberFollowListResponse.builder()
                .memberId(memberId)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImagePath(member.getProfileImagePath())
                .profileImageName(member.getProfileImageName())
                .isFollowedByLoginMember(isFollowedByLoginMember)
                .isLoginMember(isLoginMember)
                .build();
    }

}
