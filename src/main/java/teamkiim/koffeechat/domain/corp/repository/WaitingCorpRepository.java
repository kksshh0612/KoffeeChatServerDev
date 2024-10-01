package teamkiim.koffeechat.domain.corp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.WaitingCorp;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.List;

public interface WaitingCorpRepository extends JpaRepository<WaitingCorp, Long> {

    List<WaitingCorp> findByCorp(@Param("corp") Corp corp);

    boolean existsByMemberAndCorp(Member member, Corp corp);
}
