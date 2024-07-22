package teamkiim.koffeechat.domain.post.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 생성 시 클라이언트에서 보내는 request
 * 제목, 내용, 카테고리
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveDevPostServiceRequest {

    private Long id;
    private String title;
    private String bodyContent;
    private List<SkillCategory> skillCategoryList;
    private List<Long> fileIdList;
    private LocalDateTime currDateTime;

//    public List<SkillCategory> combineSkillCategory(){
//
//        List<SkillCategory> skillCategoryList = new ArrayList<>();
//
//        if (parentSkillCategoryList != null && childSkillCategoryList != null) {
//            skillCategoryList = IntStream.range(0, Math.min(parentSkillCategoryList.size(), childSkillCategoryList.size()))
//                    .mapToObj(i -> new SkillCategory(parentSkillCategoryList.get(i), childSkillCategoryList.get(i)))
//                    .collect(Collectors.toList());
//        }
//
//        return skillCategoryList;
//    }
}

