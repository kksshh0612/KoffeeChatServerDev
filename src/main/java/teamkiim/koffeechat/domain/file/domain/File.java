package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.global.auditing.BaseEntity;

import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor
public abstract class File extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    private String path;                                // 파일 저장 경로
    private String name;                                // 파일 저장명

    public File(String path, String name) {

        this.path = path;
        this.name = name;
    }


}