package teamkiim.koffeechat.domain.corp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;

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
    private VerifyStatus verifyStatus;               //이메일 도메인 검증 상태

    /**
     * 생성자
     */
    public Corp(String name, String emailDomain, VerifyStatus verifyStatus) {
        this.name = name;
        this.emailDomain = emailDomain;
        this.verifyStatus = verifyStatus;
    }

    //== 비즈니스 로직 ==//
    public void changeVerifyStatus(VerifyStatus verifyStatus) {
        this.verifyStatus = verifyStatus;
    }
}
