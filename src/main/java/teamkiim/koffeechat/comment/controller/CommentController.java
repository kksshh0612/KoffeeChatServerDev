package teamkiim.koffeechat.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.comment.controller.dto.CommentRequest;
import teamkiim.koffeechat.comment.controller.dto.ModifyCommentRequest;
import teamkiim.koffeechat.comment.domain.Comment;
import teamkiim.koffeechat.comment.service.CommentService;
import teamkiim.koffeechat.post.dev.dto.response.DevPostResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 저장", description = "사용자가 게시물에 댓글을 작성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}"),
                            @ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")
                    })
            )
    })
    public ResponseEntity<?> saveComment(@PathVariable("postId") Long postId,
                                         @Valid @RequestBody CommentRequest commentRequest, HttpServletRequest request){

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        LocalDateTime currDateTime = LocalDateTime.now();

        return commentService.saveComment(postId, commentRequest.toServiceRequest(currDateTime), memberId);
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/modify")
    @Operation(summary = "댓글 수정", description = "사용자가 자신이 작성한 댓글을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "해당 댓글을 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 댓글이 존재하지 않습니다.\"}")
                    })
            )
    })
    public ResponseEntity<?> modifyComment(@Valid @RequestBody ModifyCommentRequest modifyCommentRequest){

        LocalDateTime currDateTime = LocalDateTime.now();

        return commentService.modifyComment(modifyCommentRequest.toServiceRequest(currDateTime));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "댓글 삭제", description = "사용자가 자신이 작성한 댓글을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "해당 댓글을 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 댓글이 존재하지 않습니다.\"}")
                    })
            )
    })
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId){

        return commentService.deleteComment(commentId);
    }
}
