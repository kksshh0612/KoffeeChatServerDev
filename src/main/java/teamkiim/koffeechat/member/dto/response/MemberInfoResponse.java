package teamkiim.koffeechat.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;
import teamkiim.koffeechat.post.dev.domain.SkillCategory;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private MemberRole memberRole;
    List<SkillCategory> interestSkillCategoryList;
    private Long followerCount;
    private Long followingCount;
    private boolean isLoginMember;
    private String profileImagePath;
    private String profileImageName;

    public static MemberInfoResponse of(Member member, boolean isLoginMember){

        return MemberInfoResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .memberRole(member.getMemberRole())
                .interestSkillCategoryList(List.copyOf(member.getInterestSkillCategoryList()))
                .followerCount(member.getFollowerCount())
                .followingCount(member.getFollowingCount())
                .isLoginMember(isLoginMember)
                .profileImagePath(member.getProfileImagePath())
                .profileImageName(member.getProfileImageName())
                .build();

    }
}
