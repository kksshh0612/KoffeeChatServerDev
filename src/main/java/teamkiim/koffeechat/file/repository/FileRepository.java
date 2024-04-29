package teamkiim.koffeechat.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.file.domain.File;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findById(Long id);

    Optional<File> findByName(String name);
}
