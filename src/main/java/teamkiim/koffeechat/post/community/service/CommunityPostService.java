package teamkiim.koffeechat.post.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.post.community.domain.CommunityPost;
import teamkiim.koffeechat.post.community.domain.repository.CommunityPostRepository;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;
import teamkiim.koffeechat.post.community.dto.response.CommunityPostViewResponse;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;
import teamkiim.koffeechat.skillcategory.domain.repository.SkillCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * DTO를 Entity로 변환
     */
    public CommunityPost createDtoToEntity(PostCreateRequest dto, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);  //게시글 작성자
        CommunityPost communityPost = new CommunityPost();
        List<SkillCategory> categories= skillCategoryRepository.findCategories(dto.getSkillCategories());  //카테고리 dto-> entity
        communityPost.create(findMember.get(), dto.getTitle(), dto.getBodyContent(), categories);
        return communityPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public CommunityPostViewResponse createEntityToDto(CommunityPost post) {
        List<SkillCategory> categories= post.getSkillCategoryList();
        List<String> categoryNames= categories.stream()
                .map(SkillCategory::getName)
                .collect(Collectors.toList());
        CommunityPostViewResponse dto = new CommunityPostViewResponse();
        dto.set(post, categoryNames);

        return dto;
    }

    /**
     * 비개발 게시글 생성
     */
    @Transactional
    public CommunityPostViewResponse createCommunityPost(PostCreateRequest dto, Long memberId) {
        CommunityPost communityPost = createDtoToEntity(dto, memberId);
        communityPostRepository.save(communityPost);  //게시글 저장
        CommunityPostViewResponse communityPostDto = createEntityToDto(communityPost);

        return communityPostDto;
    }
}
