package teamkiim.koffeechat.domain.file.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DiscriminatorValue("POST")
@NoArgsConstructor
public class PostFile extends File {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;                                  // 연관 게시물

    @Builder
    public PostFile(String url, Post post) {
        super(url);
        this.post = post;
    }

    //== 연관관계 주입 매서드 ==//
    public void injectPost(Post post) {
        this.post = post;
    }
}
