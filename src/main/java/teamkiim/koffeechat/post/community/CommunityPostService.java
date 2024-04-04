package teamkiim.koffeechat.post.community;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.CommunityPostViewResponseDto;
import teamkiim.koffeechat.skillcategory.SkillCategory;
import teamkiim.koffeechat.skillcategory.SkillCategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * DTO를 Entity로 변환
     */
    public CommunityPost createDtoToEntity(PostCreateRequestDto dto) {
        CommunityPost communityPost = new CommunityPost();
        List<SkillCategory> categories= skillCategoryRepository.findCategories(dto.getSkillCategories());  //카테고리 dto-> entity
        communityPost.create(dto.getTitle(), dto.getBodyContent(), categories);
        return communityPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public CommunityPostViewResponseDto createEntityToDto(CommunityPost post) {
        CommunityPostViewResponseDto dto = new CommunityPostViewResponseDto();
        dto.set(post);

        return dto;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public CommunityPostViewResponseDto createCommunityPost(PostCreateRequestDto dto) {
        CommunityPost communityPost = createDtoToEntity(dto);
        communityPostRepository.save(communityPost);  //게시글 저장
        CommunityPostViewResponseDto communityPostDto = createEntityToDto(communityPost);

        return communityPostDto;
    }
}
