package teamkiim.koffeechat.vote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.vote.controller.dto.request.SaveVoteRecordRequest;
import teamkiim.koffeechat.vote.service.VoteService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
@Tag(name="투표 API")
public class VoteController {

    private final VoteService voteService;


    /**
     * 투표
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/{postId}/votes")
    @Operation(summary = "투표", description = "사용자가 커뮤니티 게시글 투표 항목에 투표한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 완료"),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}"),
                            @ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")
                    })
            )
    })
    public ResponseEntity<?> saveVoteRecord(@PathVariable("postId") Long postId,
                                            @Valid @RequestBody SaveVoteRecordRequest saveVoteRecordRequest, HttpServletRequest request){

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return voteService.saveVoteRecord(postId, saveVoteRecordRequest, memberId);
    }
}
