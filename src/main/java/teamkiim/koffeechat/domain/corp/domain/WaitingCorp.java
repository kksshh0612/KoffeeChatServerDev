package teamkiim.koffeechat.domain.corp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
public class WaitingCorp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_corp_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //신청한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corp_id")
    private Corp corp;  //waiting

    public WaitingCorp(Member member, Corp corp) {
        this.member = member;
        this.corp = corp;
    }
}
