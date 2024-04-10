package teamkiim.koffeechat.member;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;  // 회원 id
}
