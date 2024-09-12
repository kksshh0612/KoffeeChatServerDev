package teamkiim.koffeechat.domain.memberfollow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.memberfollow.domain.MemberFollow;

import java.util.List;
import java.util.Optional;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);

    boolean existsByFollowerAndFollowing(Member follower, Member following);

    Page<MemberFollow> findByFollowing(Member following, Pageable pageable);

    Page<MemberFollow> findByFollower(Member follower, Pageable pageable);

    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following = :member")
    List<Member> findFollowerListByFollowing(@Param("member") Member member);

    //member의 팔로워 리스트에서 검색
    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following= :member AND (f.follower.email LIKE %:keyword% OR f.follower.nickname LIKE %:keyword%)")
    Page<Member> findByFollowingAndKeyword(@Param("member") Member member, @Param("keyword") String keyword, PageRequest pageRequest);

    @Query("SELECT f.following FROM MemberFollow f WHERE f.follower= :member AND (f.following.email LIKE %:keyword% OR f.following.nickname LIKE %:keyword%)")
    Page<Member> findByFollowerAndKeyword(@Param("member") Member member, @Param("keyword") String keyword, PageRequest pageRequest);
}
