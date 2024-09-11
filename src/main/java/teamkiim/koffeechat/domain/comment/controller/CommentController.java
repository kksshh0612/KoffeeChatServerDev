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
    @PostMapping("")
    @CommentApiDocument.SaveCommentApiDoc
    public ResponseEntity<?> saveComment(@Valid @RequestBody CommentRequest commentRequest, HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime currDateTime = LocalDateTime.now();

        commentService.saveComment(commentRequest.toServiceRequest(currDateTime), memberId);

        return ResponseEntity.ok("댓글 저장 완료");
    }

    /**
     * 댓글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("")
    @CommentApiDocument.ModifyCommentApiDoc
    public ResponseEntity<?> modifyComment(@Valid @RequestBody ModifyCommentRequest modifyCommentRequest) {

        commentService.modifyComment(modifyCommentRequest.toServiceRequest());

        return ResponseEntity.ok("댓글 수정 완료");
    }

    /**
     * 댓글 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{commentId}")
    @CommentApiDocument.DeleteCommentApiDoc
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.ok("댓글 삭제 완료");
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
