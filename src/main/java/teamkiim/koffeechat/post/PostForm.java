package teamkiim.koffeechat.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 클라이언트에 보여지는 게시글 form
 */
@Getter
public abstract class PostForm {

    private Long id;
    //user
    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;
    @NotEmpty(message = "내용을 입력해주세요.")
    private String bodyContent;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime;

    //skillCategoryList
    //fileList

    /**
     * 게시글 목록 출력 시 화면에 보여줄 값 세팅 메소드
     */
    public void set(Long id, String title, String bodyContent, Long viewCount, Long likeCount, LocalDateTime createdTime, LocalDateTime modifiedTime, Long chatRoomId) {
        this.id=id;
        this.title=title;
        this.bodyContent= bodyContent;
        this.viewCount=viewCount;
        this.likeCount=likeCount;
        this.createdTime=createdTime;
        this.modifiedTime=modifiedTime;
    }

}
