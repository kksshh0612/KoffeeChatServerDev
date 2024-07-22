package teamkiim.koffeechat.domain.post.dev.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.domain.post.dev.dto.request.SaveDevPostServiceRequest;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveDevPostRequest {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
    private List<SkillCategory> skillCategoryList;
    private List<Long> fileIdList;

    public SaveDevPostServiceRequest toServiceRequest(){
        return SaveDevPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .skillCategoryList(this.skillCategoryList)
                .fileIdList(this.fileIdList)
                .build();
    }
}
