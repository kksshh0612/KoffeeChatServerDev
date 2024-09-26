package teamkiim.koffeechat.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.file.domain.PostFile;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    List<PostFile> findAllByPost(Post post);
}
