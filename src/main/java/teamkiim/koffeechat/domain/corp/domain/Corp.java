package teamkiim.koffeechat.domain.corp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Corp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "corp_id")
    private Long id;

    private String name;               //회사 이름

    private String emailDomain;        //회사 이메일 도메인

    @Enumerated(EnumType.STRING)
    private Verified verified;               //이메일 도메인 검증 상태

    /**
     * 생성자
     */
    public Corp(String name, String emailDomain, Verified verified) {
        this.name = name;
        this.emailDomain = emailDomain;
        this.verified = verified;
    }

    //== 비즈니스 로직 ==//
    public void statusModify(Verified verified) {
        this.verified = verified;
    }
}
