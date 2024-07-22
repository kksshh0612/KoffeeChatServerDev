package teamkiim.koffeechat.domain.member.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollSkillCategoryRequest {

    /*
    @NotBlank는 Enum에서 사용할 수 없음. String 타입에만 사용 가능
     */

    @NotNull(message = "상위 카테고리 선택은 필수입니다.")
    private ParentSkillCategory parentSkillCategory;
    @NotNull(message = "하위 카테고리 선택은 필수입니다.")
    private ChildSkillCategory childSkillCategory;

    public EnrollSkillCategoryServiceRequest toServiceRequest(){
        return EnrollSkillCategoryServiceRequest.builder()
                .parentSkillCategory(this.parentSkillCategory)
                .childSkillCategory(this.childSkillCategory)
                .build();
    }
}
