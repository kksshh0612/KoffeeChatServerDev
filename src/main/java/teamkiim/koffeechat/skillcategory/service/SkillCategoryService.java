package teamkiim.koffeechat.skillcategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;
import teamkiim.koffeechat.skillcategory.dto.response.SkillCategoryResponse;
import teamkiim.koffeechat.skillcategory.domain.repository.SkillCategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * name들 기준 하위 카테고리까지 조회
     */
    public List<SkillCategoryResponse> getCategories(List<String> names) {
        List<SkillCategoryResponse> categoryDtos = new ArrayList<>();
        List<SkillCategory> rootCategories = skillCategoryRepository.findCategories(names);  //탐색 시작 카테고리들

        //탐색 시작 카테고리(rootCategories)부터 재귀적으로 탐색하여 카테고리 DTO List(categoryDtos) 생성
        for (SkillCategory rootCategory : rootCategories) {
            SkillCategoryResponse categoryDto = createSkillCategoryDto(rootCategory);
            categoryDtos.add(categoryDto);
        }

        return categoryDtos;
    }

    private SkillCategoryResponse createSkillCategoryDto(SkillCategory skillCategory) {
        SkillCategoryResponse skillCategoryResponse = new SkillCategoryResponse(skillCategory.getId(), skillCategory.getName());

            //rootCategory부터 재귀적으로 탐색하여 자식들을 포함한 계층구조를 가진 skillCategoryDto 출력
            for (SkillCategory childCategory : skillCategory.getChildren()) {
                SkillCategoryResponse childCategoryDto = createSkillCategoryDto(childCategory);
                skillCategoryResponse.addChild(childCategoryDto);
            }

        return skillCategoryResponse;
    }
}
