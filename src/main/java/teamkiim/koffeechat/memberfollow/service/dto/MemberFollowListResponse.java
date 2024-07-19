package teamkiim.koffeechat.memberfollow.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFollowListResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private String profileImagePath;
    private String profileImageName;
    private boolean isFollowedByLoginMember;                            //로그인된 사용자가 팔로우하는 회원
    private boolean isLoginMember;                                      //팔로우 목록에 로그인된 사용자가 포함된 경우

    public static MemberFollowListResponse of(Member member, boolean isFollowedByLoginMember, boolean isLoginMember ){

        return MemberFollowListResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImagePath(member.getProfileImagePath())
                .profileImageName(member.getProfileImageName())
                .isFollowedByLoginMember(isFollowedByLoginMember)
                .isLoginMember(isLoginMember)
                .build();
    }

}
