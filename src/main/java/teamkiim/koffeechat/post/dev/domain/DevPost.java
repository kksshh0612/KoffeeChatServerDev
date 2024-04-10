package teamkiim.koffeechat.post.dev.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.skillcategory.SkillCategory;

import java.util.List;

@Entity
@DiscriminatorValue("Dev")
@Getter @Setter
@NoArgsConstructor
public class DevPost extends Post {

    private Long chatRoomId;  //해당 게시글 채팅방 id

    /**
     * 게시글 제목, 내용, 카테고리 수정
     */
    @Override
    public void update(PostCreateRequestDto postDto, List<SkillCategory> categoryList) {
        super.update(postDto, categoryList);
    }

}
