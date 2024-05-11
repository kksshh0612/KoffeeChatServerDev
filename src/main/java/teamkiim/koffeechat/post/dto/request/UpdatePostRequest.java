package teamkiim.koffeechat.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 수정 시 클라이언트에서 보내는 request
 * 제목, 내용, 카테고리
 */
@Data
public class UpdatePostRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;

    private List<String> skillCategories= new ArrayList<>();  // 해시태그
}
