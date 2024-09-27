package teamkiim.koffeechat.domain.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import teamkiim.koffeechat.domain.post.dev.domain.DevPost;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevPostSearchListResponse {

    private long postCnt;  //검색된 게시글 총 개수
    private List<DevPostListResponse> postList;

    public static DevPostSearchListResponse of(long postCnt, Page<DevPost> devPostList) {

        return DevPostSearchListResponse.builder()
                .postCnt(postCnt)
                .postList(devPostList.stream().map(DevPostListResponse::of).toList())
                .build();

    }

}