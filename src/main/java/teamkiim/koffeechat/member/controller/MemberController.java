package teamkiim.koffeechat.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.global.Auth;
import teamkiim.koffeechat.member.controller.dto.EnrollSkillCategoryRequest;
import teamkiim.koffeechat.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.member.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 관심 기술 카테고리 등록
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/enroll-skills")
    @Operation(summary = "회원의 관심 기술 카테고리 저장", description = "사용자가 자신의 관심 기술 카테고리를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 게시글의 PK를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 게시글을 쓰려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    public ResponseEntity<?> enrollSkils(@Valid @RequestBody List<EnrollSkillCategoryRequest> enrollSkillCategoryRequest,
                                         HttpServletRequest request){

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<EnrollSkillCategoryServiceRequest> serviceRequestList = enrollSkillCategoryRequest.stream()
                .map(EnrollSkillCategoryRequest::toServiceRequest)
                .collect(Collectors.toList());

        return memberService.enrollSkillCategory(memberId, serviceRequestList);
    }
}
