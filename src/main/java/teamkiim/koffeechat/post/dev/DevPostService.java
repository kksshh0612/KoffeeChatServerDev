package teamkiim.koffeechat.post.dev;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.post.Post;
import teamkiim.koffeechat.post.PostRepository;
import teamkiim.koffeechat.post.PostService;

import java.util.List;

@Service("devPostService")
@Transactional
public class DevPostService extends PostService {

    private final DevPostRepository devPostRepository;

    public DevPostService(@Qualifier("devPostRepository") PostRepository postRepository, DevPostRepository devPostRepository) {
        super(postRepository);
        this.devPostRepository = devPostRepository;
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public Long createDevPost(DevPost post) {
        devPostRepository.save(post);  //게시글 저장

        return post.getId();
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPost> findDevPosts() {
        return devPostRepository.findAllDev();
    }

}
