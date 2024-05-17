package teamkiim.koffeechat.skillcategory.dto;

import lombok.Getter;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;

@Getter
public class SkillCategoryDto {
    private String name;  //스킬 카테고리 이름

    public SkillCategoryDto(SkillCategory skillCategory) {
        name=skillCategory.getName();
    }
}
