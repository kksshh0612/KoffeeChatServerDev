package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.global.auditing.BaseEntity;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor
public abstract class File extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String url;                                 // 파일 저장 url

    public File(String url) {
        this.url = url;
    }
}