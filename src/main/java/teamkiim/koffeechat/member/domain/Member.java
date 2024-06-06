package teamkiim.koffeechat.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import teamkiim.koffeechat.post.dev.domain.SkillCategory;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Slf4j
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<SkillCategory> interestSkillCategoryList = new ArrayList<>();

    @Transient
    private final String profileImagePath = "PROFILE";

    private String profileImageName;

    @Builder
    public Member(String email, String password, String nickname, MemberRole memberRole,
                  List<SkillCategory> interestSkillCategoryList, String profileImageName) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberRole = memberRole;
        if(interestSkillCategoryList != null){
            this.interestSkillCategoryList.addAll(interestSkillCategoryList);
        }
        this.profileImageName = profileImageName;
    }

    //== 비지니스 로직 ==//

    /**
     * 비밀번호 암호화
     */
    public void encodePassword(PasswordEncoder passwordEncoder){

        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * 비밀번호 일치 확인
     */
    public boolean matchPassword(PasswordEncoder passwordEncoder, String password){

        if(passwordEncoder.matches(password, this.password)) return true;
        else return false;
    }

    /**
     * 관심 기술 등록
     */
    public void enrollSkillCategory(List<SkillCategory> skillCategoryList){

        this.interestSkillCategoryList.clear();
        if(skillCategoryList != null){
            this.interestSkillCategoryList.addAll(skillCategoryList);
        }
    }

    public void enrollProfileImage(String profileImageName){
        this.profileImageName = profileImageName;
    }

    /**
     * 회원 정보 수정
     */
    public void modify(String nickname, MemberRole memberRole){

        this.nickname = nickname;
        this.memberRole = memberRole;
    }
}
