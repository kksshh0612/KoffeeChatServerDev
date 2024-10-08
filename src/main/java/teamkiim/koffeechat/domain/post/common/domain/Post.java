package teamkiim.koffeechat.domain.post.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.domain.PostFile;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.tag.domain.PostTag;
import teamkiim.koffeechat.domain.tag.domain.Tag;
import teamkiim.koffeechat.global.auditing.BaseEntity;

import java.time.LocalDateTime;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                                      // 작성자

    @Enumerated(EnumType.STRING)
    PostCategory postCategory;                                  // 개발/커뮤니티

    private String title;                                       // 제목

    @Column(columnDefinition = "TEXT")
    private String bodyContent;                                 // 본문

    private long viewCount;                                     // 조회수
    private long likeCount;                                     // 좋아요 수
    private long bookmarkCount;                                 // 북마크 수

    private boolean isEditing;                                  // 작성중인 글인지
    private boolean deleted = false;                            // delete 여부 (Default false)

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> fileList = new ArrayList<>();             //파일 리스트

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();       //댓글 리스트

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTagList = new ArrayList<>();       // 해시태그 리스트


    protected Post(Member member, PostCategory postCategory, String title, String bodyContent, boolean isEditing) {

        this.member = member;
        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        this.isEditing = isEditing;
    }

    //== 연관관계 편의 매서드 ==//

    public void addFile(PostFile file) {
        this.fileList.add(file);
        file.injectPost(this);
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
        comment.injectPost(this);
    }

    public void addTag(Tag tag) {
        PostTag postTag = new PostTag(this, tag);
        this.postTagList.add(postTag);
        tag.getPostTagList().add(postTag);
    }

    public void removeTag(PostTag postTag) {
        postTagList.remove(postTag);
        postTag.getTag().getPostTagList().remove(postTag);
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
    protected void complete(PostCategory postCategory, String title, String bodyContent, LocalDateTime createdTime) {
        this.postCategory = postCategory;
        this.title = title;
        this.bodyContent = bodyContent;
        isEditing = false;
        updateCreatedTime(createdTime);
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

    /**
     * 게시글 삭제 (soft delete)
     */
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
