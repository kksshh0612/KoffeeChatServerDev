package teamkiim.koffeechat.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.comment.domain.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInfoDto {

    private Long id;
    private String content;
    private String nickname;
    private String profileImagePath;
    private String profileImageName;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static CommentInfoDto of(Comment comment){
        return CommentInfoDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .profileImagePath(comment.getMember().getProfileImagePath())
                .profileImageName(comment.getMember().getProfileImageName())
                .createdTime(comment.getCreatedTime())
                .modifiedTime(comment.getModifiedTime())
                .build();
    }
}
