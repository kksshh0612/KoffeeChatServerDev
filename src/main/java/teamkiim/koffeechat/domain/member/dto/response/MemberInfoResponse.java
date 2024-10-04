package teamkiim.koffeechat.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponse {

    private String memberId;
    private String email;
    private String nickname;
    private MemberRole memberRole;
    List<SkillCategory> interestSkillCategoryList;
    private long followerCount;
    private long followingCount;
    private Boolean isLoginMember;
    private Boolean isFollowingMember;
    private String profileImagePath;
    private String profileImageName;

    private boolean isCorpVerified;  // 현직자 인증 여부
    private String corpName;

    public static MemberInfoResponse of(Member member, String memberId, boolean isLoginMember, Boolean isFollowingMember, boolean isCorpVerified) {
        return MemberInfoResponse.builder()
                .memberId(memberId)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .memberRole(member.getMemberRole())
                .interestSkillCategoryList(List.copyOf(member.getInterestSkillCategoryList()))
                .followerCount(member.getFollowerCount())
                .followingCount(member.getFollowingCount())
                .isLoginMember(isLoginMember)
                .isFollowingMember(isFollowingMember)
                .profileImagePath(member.getProfileImagePath())
                .profileImageName(member.getProfileImageName())
                .isCorpVerified(isCorpVerified)
                .corpName(isCorpVerified ? member.getCorpName() : null)
                .build();
    }
}
