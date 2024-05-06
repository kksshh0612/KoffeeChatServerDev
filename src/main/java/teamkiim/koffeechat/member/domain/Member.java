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
    private MemberRole role;

//    private List<Long> chatRoomIdList = new ArrayList<>();

    @ElementCollection
    private List<SkillCategory> interestSkillCategoryList = new ArrayList<>();

    @Builder
    private Member(String email, String password, String nickname, MemberRole role, List<SkillCategory> interestSkillCategoryList) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.interestSkillCategoryList = interestSkillCategoryList;
    }

    //== 비지니스 로직 ==//

    /**
     * Member 생성 매서드
     * @param role 권한
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @param interestSkillCategoryList 관심 기술 리스트
     * @return
     */
    public Member create(String email, String password, String nickname, MemberRole role, List<SkillCategory> interestSkillCategoryList){
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(role)
                .interestSkillCategoryList(interestSkillCategoryList)
                .build();
    }

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

}
