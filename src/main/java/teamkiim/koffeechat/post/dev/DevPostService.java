package teamkiim.koffeechat.post.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.DevPostViewResponseDto;
import teamkiim.koffeechat.skillcategory.SkillCategory;
import teamkiim.koffeechat.skillcategory.SkillCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;
    private final SkillCategoryRepository skillCategoryRepository;

    /**
     * DTO를 Entity로 변환
     */
    public DevPost createDtoToEntity(PostCreateRequestDto dto) {
        DevPost devPost = new DevPost();
        //카테고리 dto-> entity
        List<SkillCategory> categories= skillCategoryRepository.findCategories(dto.getSkillCategories());
        devPost.create(dto.getTitle(), dto.getBodyContent(), categories);  // 개발 post 데이터 setting
        return devPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public DevPostViewResponseDto createEntityToDto(DevPost post) {
        List<SkillCategory> categories= post.getSkillCategoryList();
        List<String> categoryNames= categories.stream()
                .map(SkillCategory::getName)
                .collect(Collectors.toList());
        DevPostViewResponseDto dto = new DevPostViewResponseDto();
        dto.set(post, categoryNames);

        return dto;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public DevPostViewResponseDto createDevPost(PostCreateRequestDto dto) {
        DevPost devPost = createDtoToEntity(dto);
        System.out.println(devPost.getSkillCategoryList());
        devPostRepository.save(devPost);  //게시글 저장
        DevPostViewResponseDto devPostDto = createEntityToDto(devPost);

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
    public List<DevPost> findDevPosts() {
        return devPostRepository.findAllDev();
    }

    /**
     * 제목으로 게시글 조회
     */

    /**
     * 게시글 제목, 내용, 수정 시간 수정
     */
    @Transactional
    public DevPostViewResponseDto updatePost(Long postId, String title, String bodyContent) {
        DevPost findDev = devPostRepository.findOneDev(postId);
        findDev.update(title, bodyContent);
        DevPostViewResponseDto dto = createEntityToDto(findDev);
        return dto;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public boolean deletePost(Long postId) {
        devPostRepository.deleteById(postId);
        return true;
    }
}
