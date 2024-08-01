package teamkiim.koffeechat.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    @Query("select m from Member m left join fetch m.interestSkillCategoryList where m.id = :id")
    Optional<Member> findByIdWithSkillCategories(@Param("id") Long id);

    Optional<Member> findByEmail(String email);
}
