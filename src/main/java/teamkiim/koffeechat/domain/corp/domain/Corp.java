package teamkiim.koffeechat.domain.corp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Corp {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "corp_id")
    private Long id;

    private String corpName;

    private String corpEmailDomain;
}
