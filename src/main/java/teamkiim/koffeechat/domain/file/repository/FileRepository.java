package teamkiim.koffeechat.domain.file.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.file.domain.File;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findById(Long id);

}
