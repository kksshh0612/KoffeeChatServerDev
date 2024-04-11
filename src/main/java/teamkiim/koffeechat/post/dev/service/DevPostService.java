package teamkiim.koffeechat.post.dev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import teamkiim.koffeechat.exception.UnauthorizedAccessException;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.dev.domain.DevPost;
import teamkiim.koffeechat.post.dev.domain.repository.DevPostRepository;
import teamkiim.koffeechat.post.dev.dto.response.DevPostViewResponse;
import teamkiim.koffeechat.post.dto.request.PostCreateRequest;
import teamkiim.koffeechat.skillcategory.domain.SkillCategory;
import teamkiim.koffeechat.skillcategory.domain.repository.SkillCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 개발 게시글 관련 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;
    private final MemberRepository memberRepository;
    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * DTO를 Entity로 변환
     */
    public DevPost createDtoToEntity(PostCreateRequest dto, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);  //게시글 작성자
        DevPost devPost = new DevPost();
        //카테고리 dto-> entity
        List<SkillCategory> categories= skillCategoryRepository.findCategories(dto.getSkillCategories());
        devPost.create(findMember.get(), dto.getTitle(), dto.getBodyContent(), categories);  // 개발 post 데이터 setting
        return devPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public DevPostViewResponse createEntityToDto(DevPost post) {
        List<SkillCategory> categories= post.getSkillCategoryList();
        List<String> categoryNames= categories.stream()
                .map(SkillCategory::getName)
                .collect(Collectors.toList());
        DevPostViewResponse dto = new DevPostViewResponse();
        dto.set(post, categoryNames);

        return dto;
    }

    /**
     * 개발 게시글 생성
     */
    @Transactional
    public DevPostViewResponse createDevPost(PostCreateRequest dto, Long memberId) {
        DevPost devPost = createDtoToEntity(dto, memberId);
        devPostRepository.save(devPost);  //게시글 저장
        DevPostViewResponse devPostDto = createEntityToDto(devPost);

        return devPostDto;
    }

    /**
     * 게시글 한 개 조회
     */
    public Post findOne(Long postId) {
        return devPostRepository.findOneDev(postId);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPostViewResponse> findDevPosts() {
        List<DevPost> posts= devPostRepository.findAllDev();

        List<DevPostViewResponse> dtoList = posts.stream()
                .map(post->{
                    DevPostViewResponse dto = createEntityToDto(post);
                    return dto;
                })
                .collect(Collectors.toList());
        return dtoList;
    }

    /**
     * 제목으로 게시글 조회
     */

    /**
     * 카테고리로 게시글 리스트 조회
     */
    public List<DevPostViewResponse> findDevPostsByCategories(List<String> categoryNames) {
        List<DevPost> posts = devPostRepository.findByCategories(categoryNames);
        List<DevPostViewResponse> dtoList = posts.stream()
                .map(post->{
                    DevPostViewResponse dto = createEntityToDto(post);
                    return dto;
                })
                .collect(Collectors.toList());
        return dtoList;

    }

    /**
     * 게시글 제목, 내용, 수정 시간 수정
     */
    @Transactional
    public DevPostViewResponse updatePost(Long postId, PostCreateRequest postDto, Long memberId) {
        DevPost findDev = devPostRepository.findOneDev(postId);  // post 찾기
        List<SkillCategory> categories = skillCategoryRepository.findCategories(postDto.getSkillCategories());
        findDev.update(postDto, categories);
        DevPostViewResponse dto = createEntityToDto(findDev);
        return dto;
    }

}
