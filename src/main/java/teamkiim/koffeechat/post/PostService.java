package teamkiim.koffeechat.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 공통 기능에 대한 서비스
 * 게시글 수정, 삭제
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글 삭제
     */
    @Transactional
    public boolean deletePost(Long postId) {
        postRepository.deleteById(postId);
        return true;
    }
}
