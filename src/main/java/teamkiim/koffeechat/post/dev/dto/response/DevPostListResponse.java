package teamkiim.koffeechat.post.dev.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.post.dev.domain.DevPost;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DevPostListResponse {

    private Long id;                                // PK
    private String title;                           // 제목
    private String bodyContent;                     // 본문
    private Long viewCount;                         // 조회수
    private Long likeCount;                         // 좋아요 수
    private LocalDateTime createdTime;              // 작성 시간
    private String nickname;                        // 작성자 닉네임
    private String profileImagePath;                // 작성자 프로필 이미지 path
    private String profileImageName;                // 작성자 프로필 이미지 이름

    private String imagePath;                       // 이미지 경로
    private String imageName;                       // 이미지 이름

    @JsonIgnore
    private File imageInfo;

    public static DevPostListResponse of(DevPost devPost){

        DevPostListResponse response = DevPostListResponse.builder()
                .id(devPost.getId())
                .title(devPost.getTitle())
                .bodyContent(devPost.getBodyContent())
                .viewCount(devPost.getViewCount())
                .likeCount(devPost.getLikeCount())
                .createdTime(devPost.getCreatedTime())
                .nickname(devPost.getMember().getNickname())
                .profileImagePath(devPost.getMember().getProfileImagePath())
                .profileImageName(devPost.getMember().getProfileImageName())
                .imagePath(null)
                .imageName(null)
                .build();

        if(!devPost.getFileList().isEmpty()){
            response.setImageInfo(devPost.getFileList().get(0));
        }

        return response;
    }

    private void setImageInfo(File file){

        this.imagePath = file.getPath();
        this.imageName = file.getName();
    }
}
