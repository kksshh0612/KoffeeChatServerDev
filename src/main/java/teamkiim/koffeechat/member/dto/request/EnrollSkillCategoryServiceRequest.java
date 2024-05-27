package teamkiim.koffeechat.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.post.dev.domain.SkillCategory;

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
