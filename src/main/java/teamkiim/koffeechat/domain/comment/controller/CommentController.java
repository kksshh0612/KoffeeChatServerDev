package teamkiim.koffeechat.domain.comment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.domain.comment.controller.dto.request.CommentRequest;
import teamkiim.koffeechat.domain.comment.controller.dto.request.ModifyCommentRequest;
import teamkiim.koffeechat.domain.comment.controller.dto.response.MyCommentListResponse;
import teamkiim.koffeechat.domain.comment.service.CommentService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{postId}/comments")
    @CommentApiDocument.SaveCommentApiDoc
    public ResponseEntity<?> saveComment(@PathVariable("postId") Long postId,
                                         @Valid @RequestBody CommentRequest commentRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime currDateTime = LocalDateTime.now();

        return commentService.saveComment(postId, commentRequest.toServiceRequest(currDateTime), memberId);
    }

    /**
     * 댓글 수정
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/modify")
    @CommentApiDocument.ModifyCommentApiDoc
    public ResponseEntity<?> modifyComment(@Valid @RequestBody ModifyCommentRequest modifyCommentRequest) {

        LocalDateTime currDateTime = LocalDateTime.now();

        return commentService.modifyComment(modifyCommentRequest.toServiceRequest(currDateTime));
    }

    /**
     * 댓글 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/delete/{commentId}")
    @CommentApiDocument.DeleteCommentApiDoc
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {

        return commentService.deleteComment(commentId);
    }

    /**
     * 마이페이지 내가 쓴 댓글 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("/my")
    @CommentApiDocument.MyCommentListApiDoc
    public ResponseEntity<?> findMyCommentList(@RequestParam("page") int page, @RequestParam("size") int size, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MyCommentListResponse> myCommentListResponseList = commentService.findMyCommentList(memberId, page, size);

        return ResponseEntity.ok(myCommentListResponseList);
    }
}
