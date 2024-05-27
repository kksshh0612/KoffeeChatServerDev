package teamkiim.koffeechat.post.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.comment.domain.Comment;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.member.domain.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor
public abstract class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                                      // 작성자

    @Enumerated(EnumType.STRING)
    PostCategory postCategory;

    private String title;                                       // 제목
    private String bodyContent;                                 // 본문
    private Long viewCount;                                     // 조회수
    private Long likeCount;                                     // 좋아요 수
    private LocalDateTime createdTime;                          // 작성 시간
    private LocalDateTime modifiedTime;                         // 수정 시간

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> fileList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    protected Post(Member member, PostCategory postCategory, String title, String bodyContent,
                   Long viewCount, Long likeCount, LocalDateTime createdTime, LocalDateTime modifiedTime) {

        this.member = member;
        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    //== 연관관계 편의 매서드 ==//

    public void addFile(File file){
        this.fileList.add(file);
        file.injectPost(this);
    }

    public void addComment(Comment comment){
        commentList.add(comment);
        comment.injectPost(this);
    }

    //========================//

    //== 비지니스 로직 ==//

    /**
     * 게시글 완성
     * @param postCategory 카테고리
     * @param title 제목
     * @param bodyContent 본문
     * @param createdTime 작성 시간
     */
    protected void complete(PostCategory postCategory, String title, String bodyContent, LocalDateTime createdTime){

        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.createdTime = createdTime;
        this.modifiedTime = null;
    }

    /**
     * 게시글 수정
     * @param title 제목
     * @param bodyContent 본문
     * @param modifiedTime 수정 시간
     */
    protected void modify(String title, String bodyContent, LocalDateTime modifiedTime){

        this.title = title;
        this.bodyContent = bodyContent;
        this.modifiedTime = modifiedTime;
    }

    /**
     * 좋아요 토글 기능 : likeCount update
     * removeLike(), addLike()
     */
    public void addLike() {
        this.likeCount++;
    }
    public void removeLike() {
        this.likeCount--;
    }

}
