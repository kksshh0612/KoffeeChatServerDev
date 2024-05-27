package teamkiim.koffeechat.post.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.domain.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DevPostResponse {

    private Long id;
    private String title;
    private String bodyContent;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<ImageFileInfoDto> imageFileInfoDtoList = new ArrayList<>();

    public static DevPostResponse of(Post devPost, List<ImageFileInfoDto> imageFileInfoDtoList){

        return DevPostResponse.builder()
                .id(devPost.getId())
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .createdTime(devPost.getCreatedTime())
                .modifiedTime(devPost.getModifiedTime())
                .imageFileInfoDtoList(imageFileInfoDtoList)
                .build();
    }

}
