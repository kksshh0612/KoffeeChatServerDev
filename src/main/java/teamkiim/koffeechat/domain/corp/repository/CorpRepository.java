package teamkiim.koffeechat.domain.corp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;

import java.util.List;
import java.util.Optional;

public interface CorpRepository extends JpaRepository<Corp, Long> {

    Optional<Corp> findByCorpNameAndCorpEmailDomain(String corpName, String corpEmailDomain);

    List<Corp> findAllByCorpNameContainingIgnoreCaseAndVerified(String corpName, Verified verified);

    List<Corp> findAllByCorpEmailDomainContainingIgnoreCaseAndVerified(String corpDomain, Verified verified);
}
