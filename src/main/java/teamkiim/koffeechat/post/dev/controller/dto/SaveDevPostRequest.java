package teamkiim.koffeechat.post.dev.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.post.dev.dto.request.SaveDevPostServiceRequest;

import java.time.LocalDateTime;
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
    private List<ParentSkillCategory> parentSkillCategoryList;
    private List<ChildSkillCategory> childSkillCategoryList;
    private List<Long> fileIdList;

    public SaveDevPostServiceRequest toServiceRequest(LocalDateTime currDateTime){
        return SaveDevPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .parentSkillCategoryList(this.parentSkillCategoryList)
                .childSkillCategoryList(this.childSkillCategoryList)
                .fileIdList(this.fileIdList)
                .currDateTime(currDateTime)
                .build();
    }
}
