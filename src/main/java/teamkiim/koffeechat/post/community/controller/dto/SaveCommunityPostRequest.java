package teamkiim.koffeechat.post.community.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.community.dto.request.SaveCommunityPostServiceRequest;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveCommunityPostRequest {

    private Long id;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
    private List<Long> fileIdList;

    public SaveCommunityPostServiceRequest toServiceRequest(LocalDateTime currDateTime){
        return SaveCommunityPostServiceRequest.builder()
                .id(this.id)
                .title(this.title)
                .bodyContent(this.bodyContent)
                .fileIdList(this.fileIdList)
                .currDateTime(currDateTime)
                .build();
    }
}
