package teamkiim.koffeechat.skillcategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.skillcategory.service.SkillCategoryService;
import teamkiim.koffeechat.skillcategory.dto.response.SkillCategoryResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    /**
     * 카테고리 리스트 반환 메소드
     * categoryNames 부터 자식 카테고리들을 출력한다.
     */
    @GetMapping("/")
    @Operation(summary = "카테고리 리스트 목록 조회", description = "카테고리 리스트 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 리스트 반환 성공")
    })
    public ResponseEntity<List<SkillCategoryResponse>> categoryList(@RequestParam("category-names") List<String> categoryNames) {
        List<SkillCategoryResponse> categories = skillCategoryService.getCategories(categoryNames);
        return ResponseEntity.ok(categories);
    }
}
