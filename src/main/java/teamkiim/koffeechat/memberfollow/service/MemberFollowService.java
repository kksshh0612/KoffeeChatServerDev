package teamkiim.koffeechat.memberfollow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.memberfollow.domain.MemberFollow;
import teamkiim.koffeechat.memberfollow.repository.MemberFollowRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFollowService {

    private final MemberRepository memberRepository;
    private final MemberFollowRepository memberFollowRepository;

    public boolean isMemberFollowed(Member member, Member followingMember) {
        if (memberFollowRepository.findByFollowerAndFollowing(member, followingMember).isPresent()) return true;
        return false;
    }

    /**
     * 구독 -> 팔로우, 언팔로우
     * @param memberId 구독 누른 회원 PK
     * @param followingMemberId member가 구독한 회원 PK
     * @return Long -> followingMember의 follower 수
     */
    @Transactional
    public ResponseEntity<?> memberFollow(Long memberId, Long followingMemberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Member followingMember = memberRepository.findById(followingMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (isMemberFollowed(member, followingMember)) {  //이미 구독 중이면 -> 구독 취소
            unfollow(member, followingMember);
            member.removeFollowingCount();          //회원의 팔로잉 수 --
            followingMember.removeFollowerCount();  //회원이 구독 취소 한 회원의 팔로워 수 --
        }else{
            follow(member, followingMember);        //구독
            member.addFollowingCount();             //회원의 팔로잉 수 ++
            followingMember.addFollowerCount();     //회원이 구독한 회원의 팔로워 수 ++
        }
        return ResponseEntity.ok(followingMember.getFollowerCount());
    }

    /**
     * 팔로우
     */
    @Transactional
    public void follow(Member member, Member following) {
        MemberFollow follow= MemberFollow.createFollow(member, following);
        memberFollowRepository.save(follow);
    }

    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(Member member, Member following) {
        MemberFollow memberFollow = memberFollowRepository.findByFollowerAndFollowing(member, following)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_FOLLOW_NOT_FOUND));

        memberFollowRepository.delete(memberFollow);
    }
}
