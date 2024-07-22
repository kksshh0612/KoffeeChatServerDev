package teamkiim.koffeechat.domain.post.community.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.comment.domain.Comment;

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
    private boolean isMemberWritten;

    public static CommentInfoDto of(Comment comment, Long loginMemberId){

        boolean isMemberWritten = comment.getMember().getId().equals(loginMemberId);

        return CommentInfoDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .profileImagePath(comment.getMember().getProfileImagePath())
                .profileImageName(comment.getMember().getProfileImageName())
                .createdTime(comment.getCreatedTime())
                .modifiedTime(comment.getModifiedTime())
                .isMemberWritten(isMemberWritten)
                .build();
    }
}
