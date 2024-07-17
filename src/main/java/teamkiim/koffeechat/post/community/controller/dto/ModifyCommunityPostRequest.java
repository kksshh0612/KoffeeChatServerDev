package teamkiim.koffeechat.post.community.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.community.service.dto.request.ModifyCommunityPostServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCommunityPostRequest {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;

    public ModifyCommunityPostServiceRequest toServiceRequest(){
        return ModifyCommunityPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .build();
    }
}
