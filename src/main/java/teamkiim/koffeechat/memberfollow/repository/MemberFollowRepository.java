package teamkiim.koffeechat.memberfollow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.memberfollow.domain.MemberFollow;

import java.util.Optional;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);

    boolean existsByFollowerAndFollowing(Member follower, Member following);

    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following = :member")
    Page<Member> findFollowersByFollowingId(Member member, Pageable pageable);

}
