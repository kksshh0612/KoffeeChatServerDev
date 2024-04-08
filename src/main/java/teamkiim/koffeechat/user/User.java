package teamkiim.koffeechat.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class User {

    @Id @GeneratedValue
    @Column(name="user_id")
    private Long id;  // 회원 id
}
