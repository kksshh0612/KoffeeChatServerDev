package teamkiim.koffeechat.domain.post.dev.dto.request;

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
public class SkillCategoryRequest {

    private String parentSkillCategory;
    private String childSkillCategory;

    public SkillCategory convertToSkillCategory() {
        return new SkillCategory(ParentSkillCategory.valueOf(this.getParentSkillCategory().toUpperCase()), ChildSkillCategory.valueOf(this.getChildSkillCategory().toUpperCase()));
    }
}
