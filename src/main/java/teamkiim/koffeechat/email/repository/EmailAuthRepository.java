package teamkiim.koffeechat.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.email.domain.EmailAuth;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    public Optional<EmailAuth> findByEmailAndCode(String email, String randomAuthString);
}
