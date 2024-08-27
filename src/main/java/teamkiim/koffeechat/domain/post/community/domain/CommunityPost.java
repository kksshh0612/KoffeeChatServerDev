package teamkiim.koffeechat.domain.post.community.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.domain.PostCategory;

@Entity
@Getter
@DiscriminatorValue("COMMUNITY")
@NoArgsConstructor
public class CommunityPost extends Post {

    @Builder
    public CommunityPost(Member member, String title, String bodyContent, boolean isEditing) {

        super(member, PostCategory.COMMUNITY, title, bodyContent, isEditing);
    }


    //== 비지니스 로직 ==//

    /**
     * CommunityPost 완성
     *
     * @param title       제목
     * @param bodyContent 본문
     */
    public void completeCommunityPost(String title, String bodyContent) {

        complete(PostCategory.COMMUNITY, title, bodyContent);
    }

    /**
     * CommunityPost 수정
     *
     * @param title       제목
     * @param bodyContent 본문
     */
    public void modifyCommunityPost(String title, String bodyContent) {

        modify(title, bodyContent);
    }

}
