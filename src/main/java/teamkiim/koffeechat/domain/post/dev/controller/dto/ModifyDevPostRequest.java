package teamkiim.koffeechat.domain.post.dev.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.dto.request.ModifyDevPostServiceRequest;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyDevPostRequest {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
    private List<ParentSkillCategory> parentSkillCategoryList;
    private List<ChildSkillCategory> childSkillCategoryList;

    public ModifyDevPostServiceRequest toServiceRequest(){
        return ModifyDevPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .parentSkillCategoryList(this.parentSkillCategoryList)
                .childSkillCategoryList(this.childSkillCategoryList)
                .build();
    }
}
