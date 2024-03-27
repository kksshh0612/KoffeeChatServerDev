package teamkiim.koffeechat.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글 생성
     */
    @Transactional
    public Long createPost(Post post) {
        postRepository.save(post);  //게시글 저장

        return post.getId();
    }

    /**
     * 게시글 한 개 조회
     */
    public Post findOne(Long postId) {
        return postRepository.findOne(postId);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<Post> findPosts() {
        return postRepository.findAll();
    }

    /**
     * 제목으로 게시글 조회
     */

    /**
     * 게시글 제목, 내용 수정
     */
    @Transactional
    public void updatePost(Long postId, String title, String bodyContent) {}

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }


}
