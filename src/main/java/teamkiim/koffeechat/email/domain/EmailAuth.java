package teamkiim.koffeechat.email.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class EmailAuth {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_auth_id")
    private Long id;

    private String code;

    private String email;

    /**
     * 생성자
     */
    public EmailAuth(String email){

        this.code = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        this.email = email;
    }

}
