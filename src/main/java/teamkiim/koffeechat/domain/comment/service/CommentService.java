package teamkiim.koffeechat.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.comment.domain.Comment;
import teamkiim.koffeechat.domain.comment.dto.request.CommentServiceRequest;
import teamkiim.koffeechat.domain.comment.dto.request.ModifyCommentServiceRequest;
import teamkiim.koffeechat.domain.comment.repository.CommentRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 저장
     * @param postId 연관된 게시물 PK
     * @param commentServiceRequest 댓글 저장 dto
     * @param memberId 댓글 작성자 PK
     * @return ok
     */
    @Transactional
    public void saveComment(Long postId, CommentServiceRequest commentServiceRequest, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentServiceRequest.toEntity(post, member);

        Comment saveComment = commentRepository.save(comment);

        post.addComment(saveComment);               // 양방향 연관관계 주입
    }

    /**
     * 댓글 수정
     * @param modifyCommentServiceRequest 댓글 수정 dto
     * @return ok
     */
    @Transactional
    public void modifyComment(ModifyCommentServiceRequest modifyCommentServiceRequest){

        Comment comment = commentRepository.findById(modifyCommentServiceRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.modify(modifyCommentServiceRequest.getContent());
    }

    /**
     * 댓글 삭제
     * @param commentId 삭제할 댓글 PK
     * @return ok
     */
    @Transactional
    public void deleteComment(Long commentId){

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
    }
}
