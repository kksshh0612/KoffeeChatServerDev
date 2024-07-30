package teamkiim.koffeechat.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollSkillCategoryServiceRequest {

    private ParentSkillCategory parentSkillCategory;
    private ChildSkillCategory childSkillCategory;

    public SkillCategory combine(){

        return SkillCategory.builder()
                .parentSkillCategory(parentSkillCategory)
                .childSkillCategory(childSkillCategory)
                .build();
    }
}
