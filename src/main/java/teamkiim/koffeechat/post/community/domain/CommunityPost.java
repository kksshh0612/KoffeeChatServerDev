package teamkiim.koffeechat.post.community.domain;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.domain.PostCategory;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends Post {

    @Builder
    public CommunityPost(Member member, String title, String bodyContent,
                         Long viewCount, Long likeCount, Long bookmarkCount, boolean isEditing) {

        super(member, PostCategory.COMMUNITY, title, bodyContent, viewCount, likeCount, bookmarkCount, isEditing);
    }


    //== 비지니스 로직 ==//

    /**
     * CommunityPost 완성
     * @param title 제목
     * @param bodyContent 본문
     */
    public void completeCommunityPost(String title, String bodyContent){

        complete(PostCategory.COMMUNITY, title, bodyContent);
    }

    /**
     * CommunityPost 수정
     * @param title 제목
     * @param bodyContent 본문
     */
    public void modify(String title, String bodyContent){

        modify(title, bodyContent);
    }

}
