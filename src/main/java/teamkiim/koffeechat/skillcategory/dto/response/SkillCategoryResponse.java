package teamkiim.koffeechat.skillcategory.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * skill category dto
 */
@Data
@RequiredArgsConstructor
public class SkillCategoryResponse {
    private final Long id;
    private final String name;
    private final List<SkillCategoryResponse> children = new ArrayList<>();

    public void addChild(SkillCategoryResponse child) {
        children.add(child);
    }
}
