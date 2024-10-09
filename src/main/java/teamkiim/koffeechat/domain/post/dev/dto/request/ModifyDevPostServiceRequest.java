package teamkiim.koffeechat.domain.post.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyDevPostServiceRequest {

    private String title;
    private String bodyContent;

    private String visualData;

    private List<SkillCategory> skillCategoryList;

    private List<Long> fileIdList;

    private LocalDateTime currDateTime;

    private List<String> tagContentList;

}
