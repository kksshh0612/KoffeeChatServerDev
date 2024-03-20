package teamkiim.koffeechat.post;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("Dev")
@Getter
public class DevPost extends Post{

    private Long chatRoomId;  //해당 게시글 채팅방 id
}
