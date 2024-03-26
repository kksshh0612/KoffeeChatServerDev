package teamkiim.koffeechat.post.dev;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import teamkiim.koffeechat.post.PostForm;

import java.time.LocalDateTime;

@Getter
public class DevPostForm extends PostForm {

    private Long chatRoomId;

    @Override
    public void set(Long id, String title, String bodyContent, Long viewCount, Long likeCount, LocalDateTime createdTime, LocalDateTime modifiedTime, Long chatRoomId) {
        super.set(id, title, bodyContent, viewCount, likeCount, createdTime, modifiedTime, chatRoomId);
        this.chatRoomId=chatRoomId;
    }
}
