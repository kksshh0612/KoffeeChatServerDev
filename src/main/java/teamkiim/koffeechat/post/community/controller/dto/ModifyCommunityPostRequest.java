package teamkiim.koffeechat.post.community.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.community.dto.request.ModifyCommunityPostServiceRequest;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCommunityPostRequest {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;

    public ModifyCommunityPostServiceRequest toServiceRequest(LocalDateTime currDateTime){
        return ModifyCommunityPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .currDateTime(currDateTime)
                .build();
    }
}
