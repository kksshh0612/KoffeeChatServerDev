package teamkiim.koffeechat.file;

import jakarta.persistence.*;
import lombok.Getter;
import teamkiim.koffeechat.post.Post;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class File {
    @Id @GeneratedValue
    @Column(name="file_id")
    private Long id;  //file_id

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="post_id")
    private Post post;  //첨부파일이 포함된 게시글 참조

    private String originalFileName; // 첨부파일 이름
    private String saveFilePath;  //파일 저장 경로
    private String saveFileName;  //파일 저장명명

}