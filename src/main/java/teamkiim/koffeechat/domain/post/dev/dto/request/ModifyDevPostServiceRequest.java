package teamkiim.koffeechat.domain.post.dev.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyDevPostServiceRequest {

    private String title;
    private String bodyContent;

    private String visualData;

    private List<SkillCategory> skillCategoryList;

    private List<String> fileUrlList;

    private LocalDateTime currDateTime;

    private List<String> tagContentList;

}
