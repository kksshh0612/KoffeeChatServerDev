package teamkiim.koffeechat.domain.corp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;

import java.util.List;
import java.util.Optional;

public interface CorpRepository extends JpaRepository<Corp, Long> {

    @Query("SELECT c FROM Corp c WHERE c.name = :name AND c.emailDomain = :domain")
    Optional<Corp> findByNameAndEmailDomain(@Param("name") String name, @Param("domain") String emailDomain);

    @Query("SELECT c FROM Corp c WHERE c.name LIKE %:name% AND c.verified = :verified")
    List<Corp> findApprovedCorpByName(@Param("name") String name, @Param("verified") Verified verified);

    @Query("SELECT c FROM Corp c WHERE c.emailDomain LIKE %:domain% AND c.verified = :verified")
    List<Corp> findApprovedCorpByDomain(@Param("domain") String emailDomain, @Param("verified") Verified verified);

    Optional<Corp> findByEmailDomain(String domain);

    @Query("SELECT c FROM Corp c WHERE c.name LIKE %:keyword% OR c.emailDomain LIKE %:keyword%")
    List<Corp> findByKeyword(@Param("keyword") String keyword);
}
