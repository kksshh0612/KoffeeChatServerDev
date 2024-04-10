package teamkiim.koffeechat.member;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 회원 레파지토리
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    /**
     * id로 회원 조회
     */
    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }
}
