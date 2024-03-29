package teamkiim.koffeechat.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import teamkiim.koffeechat.post.dev.DevPost;

import java.time.LocalDateTime;

/**
 * 게시글 생성 response
 */
@Getter
public class DevPostViewResponseDto {
    private Long id;
    //user
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String bodyContent;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;  // 작성 시간
    private LocalDateTime modifiedTime;

    //skillCategoryList
    //fileList

//    private Long chatRoomId;

    public void set(DevPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.bodyContent = post.getBodyContent();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.createdTime = post.getCreatedTime();
        this.modifiedTime = post.getModifiedTime();
    }

    /**
     * 게시글 목록 출력 시 화면에 보여줄 값 세팅 메소드
     */
//    public void set(Long id, String title, String bodyContent, Long viewCount, Long likeCount, LocalDateTime createdTime, LocalDateTime modifiedTime, Long chatRoomId) {
////        this.id=id;
//        this.title=title;
//        this.bodyContent= bodyContent;
//        this.viewCount=viewCount;
//        this.likeCount=likeCount;
//        this.createdTime=createdTime;
//        this.modifiedTime=modifiedTime;
//    }
}
