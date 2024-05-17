package teamkiim.koffeechat.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String email;
    private String password;
    private String nickname;

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

}
