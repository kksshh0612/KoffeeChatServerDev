package teamkiim.koffeechat.domain.post.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyDevPostServiceRequest {

    private Long id;
    private String title;
    private String bodyContent;

    private String visualData;

    private List<ParentSkillCategory> parentSkillCategoryList;
    private List<ChildSkillCategory> childSkillCategoryList;

    private List<Long> fileIdList;

    private LocalDateTime currDateTime;

    private List<String> tagContentList;

    public List<SkillCategory> combineSkillCategory() {

        List<SkillCategory> skillCategoryList = new ArrayList<>();

        if (parentSkillCategoryList != null && childSkillCategoryList != null) {
            skillCategoryList = IntStream.range(0, Math.min(parentSkillCategoryList.size(), childSkillCategoryList.size()))
                    .mapToObj(i -> new SkillCategory(parentSkillCategoryList.get(i), childSkillCategoryList.get(i))).toList();
        }

        return skillCategoryList;
    }
}
