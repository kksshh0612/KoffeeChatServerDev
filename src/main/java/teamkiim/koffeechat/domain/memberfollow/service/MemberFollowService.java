package teamkiim.koffeechat.domain.memberfollow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.domain.MemberFollow;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.memberfollow.dto.MemberFollowListResponse;
import teamkiim.koffeechat.domain.memberfollow.service.dto.MemberFollowListResponse;
import teamkiim.koffeechat.domain.notification.domain.NotificationType;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.domain.notification.service.dto.request.CreateNotificationRequest;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFollowService {

    private final MemberRepository memberRepository;
    private final MemberFollowRepository memberFollowRepository;
    private final NotificationService notificationService;

    public boolean isMemberFollowed(Member member, Member followingMember) {

        if (memberFollowRepository.findByFollowerAndFollowing(member, followingMember).isPresent()) return true;

        return false;
    }

    /**
     * 사용자 팔로우 -> 팔로우, 언팔로우
     *
     * @param followerId 팔로우 누른 회원 PK
     * @param followingId member가 팔로우한 회원 PK
     */
    @Transactional
    public void followMember(Long followerId, Long followingId) {

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (isMemberFollowed(follower, following)) {            // 팔로우 취소
            unfollow(follower, following);
        } else {
            follow(follower, following);                        // 팔로우//회원이 구독한 회원의 팔로워 수 ++

            //팔로우 알림
            Long receiverId = followingMember.getId();
            String notiTitle = member.getNickname() + "님이 팔로우 신청을 했습니다.";
            String notiUrl = String.format("/member/profile?profileMemberId=%d", member.getId());
            notificationService.createNotification(CreateNotificationRequest
                    .of(member, notiTitle, null, notiUrl, NotificationType.FOLLOW), receiverId);

        }
    }

    /**
     * 팔로우
     */
    private void follow(Member follower, Member following) {

        MemberFollow follow = MemberFollow.createFollow(follower, following);
        memberFollowRepository.save(follow);

        follower.addFollowingCount();             // 팔로워의 팔로잉 수 ++
        following.addFollowerCount();             // 팔로우된 회원의 팔로워 수 ++
    }

    /**
     * 언팔로우
     */
    private void unfollow(Member follower, Member following) {

        MemberFollow memberFollow = memberFollowRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_FOLLOW_NOT_FOUND));

        memberFollowRepository.delete(memberFollow);

        follower.removeFollowingCount();            // 팔로워의 팔로잉 수 --
        following.removeFollowerCount();            // 팔로우된 회원의 팔로워 수 --
    }

    /**
     * 특정 회원의 팔로워 리스트 조회
     *
     * @param memberId          조회할 대상 회원 PK
     * @param loginMemberId     로그인 회원 PK
     * @param page              페이지 번호
     * @param size              페이지 당 조회할 데이터 수
     * @return List<MemberFollowListResponse>
     */
    public List<MemberFollowListResponse> findFollowerList(Long memberId, Long loginMemberId, int page, int size) {

        Member member = null;

        // member를 팔로우하는 follower들 찾기
        if(memberId == null){
            member = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        }
        else{
            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 로그인한 회원이 null이 아닌 경우 미리 조회
        Member loginMember = (loginMemberId == null) ? null : memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Member> followerList = memberFollowRepository.findByFollowing(member, pageRequest).stream()
                .map(MemberFollow::getFollower)
                .collect(Collectors.toList());

        return followerList.stream()
                .map(follower -> {
                    boolean isFollowedByLoginMember = (loginMember != null) && memberFollowRepository.existsByFollowerAndFollowing(loginMember, follower);
                    boolean isLoginMember = follower.equals(loginMember);
                    return MemberFollowListResponse.of(follower, isFollowedByLoginMember, isLoginMember);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 팔로잉 리스트 조회
     *
     * @param memberId          조회할 대상 회원 PK
     * @param loginMemberId     로그인 회원 PK
     * @param page              페이지 번호
     * @param size              페이지 당 조회할 데이터 수
     * @return List<MemberFollowListResponse>
     */
    public List<MemberFollowListResponse> findFollowingList(Long memberId, Long loginMemberId, int page, int size) {

        Member member = null;

        // member가 팔료우하는 following들 찾기
        if(memberId == null){
            member = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        }
        else{
            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 로그인한 회원이 null이 아닌 경우 미리 조회
        Member loginMember = (loginMemberId == null) ? null : memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Member> followingList = memberFollowRepository.findByFollower(member, pageRequest).stream()
                .map(MemberFollow::getFollowing)
                .collect(Collectors.toList());

        return followingList.stream()
                .map(following -> {
                    boolean isFollowedByLoginMember = (loginMember != null) && memberFollowRepository.existsByFollowerAndFollowing(loginMember, following);
                    boolean isLoginMember = following.equals(loginMember);
                    return MemberFollowListResponse.of(following, isFollowedByLoginMember, isLoginMember);
                })
                .collect(Collectors.toList());
    }
}
