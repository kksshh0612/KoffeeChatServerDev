package teamkiim.koffeechat.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 생성 시 클라이언트에서 보내는 request
 */
@Getter
public class PostCreateRequestDto {
    //user
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
//    private Long viewCount;
//    private Long likeCount;
//    private LocalDateTime createdTime;  // 작성 시간
//    private LocalDateTime modifiedTime;

    private List<String> skillCategories= new ArrayList<>();  // 해시태그
    //fileList

//    private Long chatRoomId;

}
