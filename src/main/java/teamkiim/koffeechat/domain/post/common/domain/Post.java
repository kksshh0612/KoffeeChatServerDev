package teamkiim.koffeechat.domain.post.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import teamkiim.koffeechat.global.auditing.BaseEntity;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor
@SQLRestriction("deleted = false")
public abstract class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                                      // 작성자

    @Enumerated(EnumType.STRING)
    PostCategory postCategory;                                  // 관심 기술

    private String title;                                       // 제목

    @Column(columnDefinition = "TEXT")
    private String bodyContent;                                 // 본문

    private Long viewCount;                                     // 조회수
    private Long likeCount;                                     // 좋아요 수
    private Long bookmarkCount;                                 // 북마크 수

    private boolean isEditing;                                  // 작성중인 글인지
    private boolean deleted = false;                            // delete 여부 (Default false)

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> fileList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    protected Post(Member member, PostCategory postCategory, String title, String bodyContent,
                   Long viewCount, Long likeCount, Long bookmarkCount, boolean isEditing) {

        this.member = member;
        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.bookmarkCount = bookmarkCount;
        this.isEditing = isEditing;
    }

    //== 연관관계 편의 매서드 ==//

    public void addFile(File file) {
        this.fileList.add(file);
        file.injectPost(this);
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
        comment.injectPost(this);
    }

    //========================//

    //== 비지니스 로직 ==//

    /**
     * 게시글 완성
     *
     * @param postCategory 카테고리
     * @param title        제목
     * @param bodyContent  본문
     */
    protected void complete(PostCategory postCategory, String title, String bodyContent) {
        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.bookmarkCount = 0L;
        isEditing = false;
    }

    /**
     * 게시글 수정
     *
     * @param title       제목
     * @param bodyContent 본문
     */
    protected void modify(String title, String bodyContent) {
        this.title = title;
        this.bodyContent = bodyContent;
    }

    public void delete() {
        this.deleted = true;
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

    /**
     * 북마크 토글 기능 : bookmarkCount update
     * removeBookmark(), addBookmark()
     */
    public void addBookmark() {
        this.bookmarkCount++;
    }

    public void removeBookmark() {
        this.bookmarkCount--;
    }

    /**
     * 조회수 증가 : viewCount update
     */
    public void addViewCount() {
        this.viewCount++;
    }

}
