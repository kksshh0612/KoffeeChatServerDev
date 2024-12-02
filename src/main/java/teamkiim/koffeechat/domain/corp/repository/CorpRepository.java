package teamkiim.koffeechat.domain.corp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;
import teamkiim.koffeechat.domain.corp.domain.Corp;

public interface CorpRepository extends JpaRepository<Corp, Long> {

    @Query("SELECT c FROM Corp c WHERE c.name = :name AND c.emailDomain = :domain")
    Optional<Corp> findByNameAndEmailDomain(@Param("name") String name, @Param("domain") String emailDomain);

    @Query("SELECT c FROM Corp c WHERE c.name LIKE %:name% AND c.verifyStatus = :verifyStatus")
    List<Corp> findApprovedCorpByName(@Param("name") String name, @Param("verifyStatus") VerifyStatus verifyStatus);

    @Query("SELECT c FROM Corp c WHERE c.emailDomain LIKE %:domain% AND c.verifyStatus = :verifyStatus")
    List<Corp> findApprovedCorpByDomain(@Param("domain") String emailDomain,
                                        @Param("verifyStatus") VerifyStatus verifyStatus);

    Optional<Corp> findByEmailDomain(String domain);

    @Query("SELECT c FROM Corp c WHERE "
            + ":keyword IS NULL OR "
            + "c.name LIKE %:keyword% OR "
            + "c.emailDomain LIKE %:keyword%")
    List<Corp> findAllByKeyword(@Param("keyword") String keyword);
}
