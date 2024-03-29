package teamkiim.koffeechat.post.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.request.PostCreateRequestDto;
import teamkiim.koffeechat.response.DevPostCreateResponseDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevPostService {

    private final DevPostRepository devPostRepository;

    /**
     * DTO를 Entity로 변환
     */
    public DevPost createDtoToEntity(PostCreateRequestDto dto) {
        DevPost devPost = new DevPost();
        devPost.create(dto.getTitle(), dto.getBodyContent());
        return devPost;
    }

    /**
     * Entity를 DTO로 변환
     */
    public DevPostCreateResponseDto createEntityToDto(DevPost post) {
        DevPostCreateResponseDto dto = new DevPostCreateResponseDto();
        dto.set(post);

        return dto;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public DevPostCreateResponseDto createDevPost(PostCreateRequestDto dto) {
        DevPost devPost = createDtoToEntity(dto);
        devPostRepository.save(devPost);  //게시글 저장
        DevPostCreateResponseDto devPostDto = createEntityToDto(devPost);

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
     * 게시글 제목, 내용 수정
     */
    @Transactional
    public void updatePost(Long postId, String title, String bodyContent) {
        Post findDev = devPostRepository.findOneDev(postId);
        findDev.update(title, bodyContent);
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
