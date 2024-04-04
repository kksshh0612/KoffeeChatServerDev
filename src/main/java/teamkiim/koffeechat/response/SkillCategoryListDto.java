package teamkiim.koffeechat.response;

import lombok.Getter;
import teamkiim.koffeechat.skillcategory.SkillCategory;

import java.util.List;

/**
 * skillcategory 목록 출력 dto
 */
@Getter
public class SkillCategoryListDto {
    private Long id;
    private String name;

    private List<SkillCategory> child;  //하위 카테고리
}
