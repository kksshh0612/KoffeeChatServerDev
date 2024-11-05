package teamkiim.koffeechat.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Entity
@NoArgsConstructor
@Getter
@Slf4j
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<SkillCategory> interestSkillCategoryList = new ArrayList<>();

    private long followerCount;                                     // 팔로워 수
    private long followingCount;                                    // 팔로잉 수

    private String profileImageUrl;                                 // 회원 프로필 이미지 url

    private String corpName;                                        // 회사 이름
    private String corpEmail;                                       // 회사 이메일

    private long unreadNotificationCount;                               // 읽지 않은 알림 갯수

    @Builder
    public Member(String email, String password, String nickname, MemberRole memberRole,
                  List<SkillCategory> interestSkillCategoryList,
                  long followerCount, long followingCount, String profileImageUrl) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberRole = memberRole;
        this.interestSkillCategoryList.addAll(Optional.ofNullable(interestSkillCategoryList).orElse(new ArrayList<>()));
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.profileImageUrl = profileImageUrl;
        this.corpName = null;
        this.corpEmail = null;
        this.unreadNotificationCount = 0;
    }

    //== 비지니스 로직 ==//

    /**
     * 비밀번호 암호화
     */
    public void encodePassword(String encodedPassword) {

        this.password = encodedPassword;
    }

    /**
     * 관심 기술 등록
     */
    public void enrollSkillCategory(List<SkillCategory> skillCategoryList) {

        this.interestSkillCategoryList.clear();
        if (skillCategoryList != null) {
            this.interestSkillCategoryList.addAll(skillCategoryList);
        }
    }

    /**
     * 이메일 업데이트
     */
    public void updateEmail(String email) {
        this.email = email;
    }

    /**
     * 프로필 이미지 등록
     */
    public void enrollProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 회원 정보 수정
     */
    public void modify(String nickname, MemberRole memberRole) {

        this.nickname = nickname;
        this.memberRole = memberRole;
    }

    /**
     * 팔로워/팔로잉 수 관리 addFollowerCount(), removeFollowerCount() addFollowingCount(), removeFollowingCount()
     */
    public void addFollowerCount() {
        this.followerCount++;
    }

    public void removeFollowerCount() {
        this.followerCount--;
    }

    public void addFollowingCount() {
        this.followingCount++;
    }

    public void removeFollowingCount() {
        this.followingCount--;
    }

    /**
     * 현직자 인증
     */
    public void authCorp(String corpName, String corpEmail) {
        this.corpName = corpName;
        this.corpEmail = corpEmail;
    }

    /**
     * 읽지 않은 알림 수 관리
     */
    public void updateUnreadNotificationCount(int unreadNotifications) {
        this.unreadNotificationCount = unreadNotifications;
    }

    public void addUnreadNotifications() {
        this.unreadNotificationCount++;
    }

    public void removeUnreadNotifications() {
        this.unreadNotificationCount--;
    }
}
