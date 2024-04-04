package teamkiim.koffeechat.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teamkiim.koffeechat.file.File;
import teamkiim.koffeechat.skillcategory.SkillCategory;
import teamkiim.koffeechat.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@Getter @Setter
@NoArgsConstructor
public abstract class Post {  //게시글

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;  //post_id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 게시글 작성자

    private String title;  // 제목
    private String bodyContent;  // 내용
    private Long viewCount;  // 조회수
    private Long likeCount;  // 좋아요 수
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime;

    @ManyToMany
    @JoinTable(name = "post_skill_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_category_id")
    )
    private List<SkillCategory> skillCategoryList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<File> fileList = new ArrayList<>();


    /**
     * 게시글 생성 값 세팅
     */
    public void create(String title, String bodyContent, List<SkillCategory> skillCategoryList) {
//        this.user=user;
        this.title=title;
        this.bodyContent=bodyContent;
        this.viewCount=0L;  //조회수 0
        this.likeCount=0L;  //좋아요 수 0
        this.createdTime = LocalDateTime.now();
        this.modifiedTime=LocalDateTime.now();
        this.skillCategoryList = skillCategoryList;  // 카테고리 해시태그

//        this.fileList
    }

    /**
     * 게시글 제목, 내용 수정 값 세팅
     */
    public void update(String title, String bodyContent) {
        this.title= title;
        this.bodyContent=bodyContent;
        this.modifiedTime = LocalDateTime.now();
    }

}
