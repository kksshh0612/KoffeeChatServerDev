package teamkiim.koffeechat.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.file.domain.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

}
