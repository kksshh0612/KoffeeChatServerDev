package teamkiim.koffeechat.post.community.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyVoteRequest {

    @NotBlank(message = "투표 제목을 입력해 주세요.")
    private String title;

    @NotEmpty(message="항목을 입력해 주세요.")
    private List<String> items;
}
