package teamkiim.koffeechat.file.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.domain.Post;

import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="post_id")
    private Post post;                                  // 연관 게시물

    private String path;                        // 파일 저장 경로
    private String name;                        // 파일 저장명

    @Builder
    public File(Post post) {
        this.post = post;
        this.path = post.getPostCategory().toString();
        this.name = UUID.randomUUID().toString();
    }

    //== 연관관계 주입 매서드 ==//
    public void injectPost(Post post){
        this.post = post;
    }
}