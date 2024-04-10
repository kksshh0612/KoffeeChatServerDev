package teamkiim.koffeechat.skillcategory.controller;

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
    public ResponseEntity<List<SkillCategoryResponse>> categoryList(@RequestParam("category-names") List<String> categoryNames) {
        List<SkillCategoryResponse> categories = skillCategoryService.getCategories(categoryNames);
        return ResponseEntity.ok(categories);
    }
}
