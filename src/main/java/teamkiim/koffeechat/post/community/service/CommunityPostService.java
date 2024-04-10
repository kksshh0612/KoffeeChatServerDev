package teamkiim.koffeechat.post.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.post.community.domain.repository.CommunityPostRepository;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostViewResponse;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;
import teamkiim.koffeechat.skillcategory.domain.repository.SkillCategoryRepository;

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
    public CommunityPost createDtoToEntity(PostCreateRequest dto) {
        CommunityPost communityPost = new CommunityPost();
        List<SkillCategory> categories= skillCategoryRepository.findCategories(dto.getSkillCategories());  //카테고리 dto-> entity
        communityPost.create(dto.getTitle(), dto.getBodyContent(), categories);
        return communityPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public CommunityPostViewResponse createEntityToDto(CommunityPost post) {
        CommunityPostViewResponse dto = new CommunityPostViewResponse();
        dto.set(post);

        return dto;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public CommunityPostViewResponse createCommunityPost(PostCreateRequest dto) {
        CommunityPost communityPost = createDtoToEntity(dto);
        communityPostRepository.save(communityPost);  //게시글 저장
        CommunityPostViewResponse communityPostDto = createEntityToDto(communityPost);

        return communityPostDto;
    }
}
