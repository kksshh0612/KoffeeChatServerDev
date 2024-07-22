package teamkiim.koffeechat.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findById(Long id);

    public Optional<Member> findByEmail(String email);
}
