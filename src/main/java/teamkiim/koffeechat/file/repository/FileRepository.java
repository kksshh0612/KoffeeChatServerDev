package teamkiim.koffeechat.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findById(Long id);

    List<File> findAllByPost(Post post);

    Optional<File> findByName(String name);
}
