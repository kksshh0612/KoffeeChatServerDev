package teamkiim.koffeechat.domain.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.util.ArrayList;
import java.util.List;

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

    private long followerCount;                                     //팔로워 수
    private long followingCount;                                    //팔로잉 수

    @Transient
    private final String profileImagePath = "PROFILE";

    private String profileImageName;

    private String corpName;                                        //회사 이름
    private String corpEmail;                                       //회사 이메일

    private long unreadNotifications;                               //읽지 않은 알림 갯수

    @Builder
    public Member(String email, String password, String nickname, MemberRole memberRole,
                  List<SkillCategory> interestSkillCategoryList,
                  long followerCount, long followingCount, String profileImageName) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberRole = memberRole;
        if (interestSkillCategoryList != null) {
            this.interestSkillCategoryList.addAll(interestSkillCategoryList);
        }
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.profileImageName = profileImageName;
        this.corpName = null;
        this.corpEmail = null;
        this.unreadNotifications = 0;
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

    public void enrollProfileImage(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    /**
     * 회원 정보 수정
     */
    public void modify(String nickname, MemberRole memberRole) {

        this.nickname = nickname;
        this.memberRole = memberRole;
    }

    /**
     * 팔로워/팔로잉 수 관리
     * addFollowerCount(), removeFollowerCount()
     * addFollowingCount(), removeFollowingCount()
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
        this.unreadNotifications = unreadNotifications;
    }

    public void addUnreadNotifications() {
        this.unreadNotifications++;
    }

    public void removeUnreadNotifications() {
        this.unreadNotifications--;
    }
}
