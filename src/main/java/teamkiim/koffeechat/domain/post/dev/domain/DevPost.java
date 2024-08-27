package teamkiim.koffeechat.domain.post.dev.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SkillCategory> skillCategoryList = new ArrayList<>();

    @Builder
    public DevPost(Member member, String title, String bodyContent, boolean isEditing, List<SkillCategory> skillCategoryList) {

        super(member, PostCategory.DEV, title, bodyContent, isEditing);
        if (skillCategoryList != null) this.skillCategoryList = List.copyOf(skillCategoryList);
    }

    //== 비지니스 로직==//

    /**
     * DevPost 완성
     *
     * @param title             제목
     * @param bodyContent       본문
     * @param skillCategoryList 관련 기술 카테고리 리스트
     */
    public void completeDevPost(String title, String bodyContent, List<SkillCategory> skillCategoryList) {

        complete(PostCategory.DEV, title, bodyContent);
        if (skillCategoryList != null) this.skillCategoryList.addAll(skillCategoryList);
    }

    /**
     * DevPost 수정
     *
     * @param title             제목
     * @param bodyContent       본문
     * @param skillCategoryList 관련 기술 카테고리 리스트
     */
    public void modify(String title, String bodyContent, List<SkillCategory> skillCategoryList) {

        modify(title, bodyContent);
        this.skillCategoryList.clear();
        if (skillCategoryList != null) this.skillCategoryList = List.copyOf(skillCategoryList);
    }

}
