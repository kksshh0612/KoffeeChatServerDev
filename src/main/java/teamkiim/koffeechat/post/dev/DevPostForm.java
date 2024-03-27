package teamkiim.koffeechat.post.dev;

import lombok.Getter;
import teamkiim.koffeechat.post.PostForm;

import java.time.LocalDateTime;

@Getter
public class DevPostForm extends PostForm {

    private Long chatRoomId;

    /**
     * 게시글 목록 출력 시 화면에 보여줄 값 세팅 메소드
     */
    @Override
    public void set(Long id, String title, String bodyContent, Long viewCount, Long likeCount, LocalDateTime createdTime, LocalDateTime modifiedTime, Long chatRoomId) {
        super.set(id, title, bodyContent, viewCount, likeCount, createdTime, modifiedTime, chatRoomId);
        this.chatRoomId=chatRoomId;
    }
}
