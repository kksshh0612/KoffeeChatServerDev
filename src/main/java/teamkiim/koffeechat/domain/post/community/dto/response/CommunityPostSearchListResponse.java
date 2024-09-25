package teamkiim.koffeechat.domain.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import teamkiim.koffeechat.domain.post.community.domain.CommunityPost;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostSearchListResponse {

    private long postCnt;  //검색된 게시글 총 개수
    private List<CommunityPostListResponse> postList;

    public static CommunityPostSearchListResponse of(long postCnt, Page<CommunityPost> communityPostList) {

        return CommunityPostSearchListResponse.builder()
                .postCnt(postCnt)
                .postList(communityPostList.stream().map(CommunityPostListResponse::of).toList())
                .build();

    }

}
