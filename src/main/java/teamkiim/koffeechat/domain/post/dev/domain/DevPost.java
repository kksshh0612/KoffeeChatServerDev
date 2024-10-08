package teamkiim.koffeechat.domain.post.dev.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 개발 게시글 엔티티
 */
@Entity
@Getter
@DiscriminatorValue("DEV")
@NoArgsConstructor
public class DevPost extends Post {

    @Column(columnDefinition = "TEXT")
    private String visualData;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SkillCategory> skillCategoryList = new ArrayList<>();

    @Builder
    public DevPost(Member member, String title, String bodyContent, boolean isEditing, String visualDate, List<SkillCategory> skillCategoryList) {

        super(member, PostCategory.DEV, title, bodyContent, isEditing);
        this.visualData = visualDate;
        if (skillCategoryList != null) this.skillCategoryList = List.copyOf(skillCategoryList);
    }

    //== 비지니스 로직==//

    /**
     * DevPost 완성
     *
     * @param title             제목
     * @param bodyContent       본문
     * @param visualData        시각자료
     * @param skillCategoryList 관련 기술 카테고리 리스트
     */
    public void completeDevPost(String title, String bodyContent, String visualData, List<SkillCategory> skillCategoryList, LocalDateTime createdTime){

        complete(PostCategory.DEV, title, bodyContent, createdTime);
        this.visualData = visualData;
        if (skillCategoryList != null) this.skillCategoryList.addAll(skillCategoryList);
    }

    /**
     * DevPost 수정
     *
     * @param title             제목
     * @param bodyContent       본문
     * @param visualData        시각자료
     * @param skillCategoryList 관련 기술 카테고리 리스트
     */
    public void modify(String title, String bodyContent, String visualData, List<SkillCategory> skillCategoryList) {

        modify(title, bodyContent);
        this.visualData = visualData;
        this.skillCategoryList.clear();
        if (skillCategoryList != null) this.skillCategoryList = List.copyOf(skillCategoryList);
    }

}
