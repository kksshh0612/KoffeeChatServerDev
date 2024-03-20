package teamkiim.koffeechat.post;

import jakarta.persistence.*;
import lombok.Getter;
import teamkiim.koffeechat.member.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@Getter
public abstract class Post {  //게시글

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;  //post_id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 게시글 작성자

    private String title;  // 제목
    private String bodyContent;  // 내용
    private Long viewCount;  // 조회수
    private Long likeCount;  // 좋아요 수
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime;

    @OneToMany
    @JoinColumn(name="skill_category_id")
    private List<SkillCategory> skillCategoryList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<File> fileList = new ArrayList<>();

}
