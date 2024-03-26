package teamkiim.koffeechat.post.dev;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teamkiim.koffeechat.post.Post;

@Entity
@DiscriminatorValue("Dev")
@Getter @Setter
@NoArgsConstructor
public class DevPost extends Post {

    private Long chatRoomId;  //해당 게시글 채팅방 id
}
