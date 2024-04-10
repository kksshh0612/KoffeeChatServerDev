package teamkiim.koffeechat.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.global.passwordEncrypt.PasswordEncryptor;
//import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String email;
    private String password;
    private String nickname;
    private String imageUrl;

    private String socialLoginId;

    //== 비지니스 로직 ==//
    /*
    비밀번호 암호화
     */
    public void encodePassword(PasswordEncryptor passwordEncryptor){

        this.password = passwordEncryptor.sha512WithSaltEncode(this.password, passwordEncryptor.getSalt());
    }

}
