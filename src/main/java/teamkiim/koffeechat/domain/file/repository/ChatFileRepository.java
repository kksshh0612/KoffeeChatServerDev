package teamkiim.koffeechat.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.file.domain.ChatFile;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
}
