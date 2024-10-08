package teamkiim.koffeechat.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.comment.controller.dto.response.MyCommentListResponse;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.comment.dto.request.CommentServiceRequest;
import teamkiim.koffeechat.domain.comment.dto.request.ModifyCommentServiceRequest;
import teamkiim.koffeechat.domain.comment.repository.CommentRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.notification.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;

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
     * @param commentServiceRequest 댓글 저장 dto
     * @param memberId              댓글 작성자 PK
     * @return ok
     */
    @Transactional
    public void saveComment(CommentServiceRequest commentServiceRequest, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(commentServiceRequest.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentServiceRequest.toEntity(post, member);

        Comment savedComment = commentRepository.save(comment);

        // 양방향 연관관계 주입
        post.addComment(savedComment);               // 양방향 연관관계 주입

        //글쓴이에게 댓글 알림 전송
        notificationService.createCommentNotification(post, member, comment);

    }

    /**
     * 댓글 수정
     *
     * @param modifyCommentServiceRequest 댓글 수정 dto
     * @return ok
     */
    @Transactional
    public void modifyComment(ModifyCommentServiceRequest modifyCommentServiceRequest) {

        Comment comment = commentRepository.findById(modifyCommentServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.modify(modifyCommentServiceRequest.getContent());
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글 PK
     * @return ok
     */
    @Transactional
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
    }

    /**
     * 로그인한 회원이 작성한 댓글 목록 조회
     *
     * @param memberId 로그인한 회원
     * @param page     페이지 번호 ( ex) 0, 1,,,, )
     * @param size     페이지 당 조회할 데이터 수
     * @return List<MyCommentListResponse>
     */
    public List<MyCommentListResponse> findMyCommentList(Long memberId, int page, int size) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));  //최근 작성한 댓글부터

        List<Comment> commentList = commentRepository.findAllByMember(member, pageRequest).getContent();

        return commentList.stream().map(comment -> MyCommentListResponse.of(comment, !(comment.getPost().isEditing() || comment.getPost().isDeleted()))).toList();
    }
}
