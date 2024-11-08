package teamkiim.koffeechat.domain.post.common.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.comment.domain.Comment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInfoDto {

    private String id;
    private String content;
    private String memberId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private boolean isMemberWritten;

    public static CommentInfoDto of(String commentId, Comment comment, String memberId, boolean isMemberWritten) {

        return CommentInfoDto.builder()
                .id(commentId)
                .content(comment.getContent())
                .memberId(memberId)
                .nickname(comment.getMember().getNickname())
                .profileImageUrl(comment.getMember().getProfileImageUrl())
                .createdTime(comment.getCreatedTime())
                .modifiedTime(comment.getModifiedTime())
                .isMemberWritten(isMemberWritten)
                .build();
    }
}
