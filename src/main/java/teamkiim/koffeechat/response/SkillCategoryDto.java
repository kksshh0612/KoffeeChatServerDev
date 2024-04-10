package teamkiim.koffeechat.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * skill category dto
 */
@Getter
@RequiredArgsConstructor
public class SkillCategoryDto {
    private final Long id;
    private final String name;
    private final List<SkillCategoryDto> children = new ArrayList<>();

    public void addChild(SkillCategoryDto child) {
        children.add(child);
    }
}
