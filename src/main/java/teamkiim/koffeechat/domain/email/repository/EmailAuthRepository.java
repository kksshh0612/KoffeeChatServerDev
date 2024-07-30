package teamkiim.koffeechat.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.email.domain.EmailAuth;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    Optional<EmailAuth> findByEmailAndCode(String email, String randomAuthString);
}
