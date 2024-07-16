package teamkiim.koffeechat.memberfollow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.memberfollow.domain.MemberFollow;

import java.util.Optional;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);
}
