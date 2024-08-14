package teamkiim.koffeechat.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.comment.dto.request.CommentServiceRequest;
import teamkiim.koffeechat.domain.comment.dto.request.ModifyCommentServiceRequest;
import teamkiim.koffeechat.domain.comment.repository.CommentRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.notification.service.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    /**
     * 댓글 저장
     *
     * @param postId                연관된 게시물 PK
     * @param commentServiceRequest 댓글 저장 dto
     * @param memberId              댓글 작성자 PK
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> saveComment(Long postId, CommentServiceRequest commentServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentServiceRequest.toEntity(post, member);

        Comment savedComment = commentRepository.save(comment);

        post.addComment(savedComment);               // 양방향 연관관계 주입

        //글쓴이에게 댓글 알림 전송
        Long writerId = post.getMember().getId();
        String notiTitle = member.getNickname() + "님이 " + post.getTitle() + "글에 댓글을 남겼습니다.";
        String notiUrl = String.format("/community-post?postId=%d", post.getId());
        notificationService.createNotification(CreateNotificationRequest
                .of(member, notiTitle, savedComment.getContent(), notiUrl, NotificationType.COMMENT), writerId);

        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 저장 완료");
    }

    /**
     * 댓글 수정
     *
     * @param modifyCommentServiceRequest 댓글 수정 dto
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> modifyComment(ModifyCommentServiceRequest modifyCommentServiceRequest) {

        Comment comment = commentRepository.findById(modifyCommentServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.modify(modifyCommentServiceRequest.getContent(), modifyCommentServiceRequest.getCurrDateTime());

        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 수정 완료");
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글 PK
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);

        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
