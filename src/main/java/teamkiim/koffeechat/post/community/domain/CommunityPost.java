package teamkiim.koffeechat.post.community.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.post.common.domain.PostCategory;
import teamkiim.koffeechat.vote.domain.Vote;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends Post {

    @Builder
    public CommunityPost(Member member, String title, String bodyContent,
                         Long viewCount, Long likeCount, Long bookmarkCount, LocalDateTime createdTime, LocalDateTime modifiedTime, boolean isEditing) {

        super(member, PostCategory.COMMUNITY, title, bodyContent, viewCount, likeCount, bookmarkCount, createdTime, modifiedTime, isEditing);
    }


    //== 비지니스 로직 ==//

    /**
     * CommunityPost 완성
     * @param title 제목
     * @param bodyContent 본문
     * @param createdTime 작성 시간
     */
    public void completeCommunityPost(String title, String bodyContent, LocalDateTime createdTime){

        complete(PostCategory.COMMUNITY, title, bodyContent, createdTime);
    }

    /**
     * CommunityPost 수정
     * @param title 제목
     * @param bodyContent 본문
     * @param modifiedTime 수정 시간
     */
    public void modify(String title, String bodyContent, LocalDateTime modifiedTime){

        modify(title, bodyContent, modifiedTime);
    }

}
