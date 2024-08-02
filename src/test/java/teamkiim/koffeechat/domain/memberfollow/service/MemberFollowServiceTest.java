package teamkiim.koffeechat.domain.memberfollow.service;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.domain.MemberFollow;
import teamkiim.koffeechat.domain.memberfollow.dto.MemberFollowListResponse;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemberFollowServiceTest extends TestSupport {

    @Autowired
    MemberFollowService memberFollowService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberFollowRepository memberFollowRepository;

    @AfterEach
    void tearDown() {
        memberFollowRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원이 팔로우 한 멤버에 대해 팔로우 여부를 조회하면 true를 반환한다.")
    @Test
    void isMemberFollowedWhenFollowed() {
        // given
        Member follower = memberRepository.save(createMember("1@email.com"));
        Member following = memberRepository.save(createMember("2@email.com"));
        memberFollowRepository.save(new MemberFollow(follower, following));

        // when
        boolean memberFollowed = memberFollowService.isMemberFollowed(follower, following);

        // then
        Assertions.assertThat(memberFollowed).isTrue();
    }

    @DisplayName("회원이 팔로우 하지 않은 멤버에 대해 팔로우 여부를 조회하면 false를 반환한다.")
    @Test
    void isMemberFollowedWhenNotFollowed() {
        // given
        Member follower = memberRepository.save(createMember("1@email.com"));
        Member following = memberRepository.save(createMember("2@email.com"));

        // when
        boolean memberFollowed = memberFollowService.isMemberFollowed(follower, following);

        // then
        Assertions.assertThat(memberFollowed).isFalse();
    }

    @DisplayName("회원이 타 회원을 팔로우한다.")
    @Test
    void followMember() {
        // given
        Member follower = memberRepository.save(createMember("1@email.com"));
        Member following = memberRepository.save(createMember("2@email.com"));

        // when
        memberFollowService.followMember(follower.getId(), following.getId());

        // then
        Optional<MemberFollow> memberFollow = memberFollowRepository.findByFollowerAndFollowing(follower, following);

        Assertions.assertThat(memberFollow).isPresent();

        Assertions.assertThat(memberFollow.get().getFollower().getId()).isEqualTo(follower.getId());
        Assertions.assertThat(memberFollow.get().getFollower().getFollowerCount()).isEqualTo(0);
        Assertions.assertThat(memberFollow.get().getFollower().getFollowingCount()).isEqualTo(1);

        Assertions.assertThat(memberFollow.get().getFollowing().getId()).isEqualTo(following.getId());
        Assertions.assertThat(memberFollow.get().getFollowing().getFollowerCount()).isEqualTo(1);
        Assertions.assertThat(memberFollow.get().getFollowing().getFollowingCount()).isEqualTo(0);
    }

    @DisplayName("회원이 타 회원을 언팔로우한다.")
    @Test
    void unFollowMember() {
        // given
        Member follower = memberRepository.save(createMember("1@email.com"));
        Member following = memberRepository.save(createMember("2@email.com"));

        memberFollowService.followMember(follower.getId(), following.getId());

        // when
        memberFollowService.followMember(follower.getId(), following.getId());

        // then
        Optional<MemberFollow> memberFollow = memberFollowRepository.findByFollowerAndFollowing(follower, following);

        Assertions.assertThat(memberFollow).isEmpty();
        Assertions.assertThat(follower.getFollowingCount()).isEqualTo(0);
        Assertions.assertThat(following.getFollowerCount()).isEqualTo(0);
    }

    @DisplayName("회원의 팔로워 목록을 조회한다.")
    @Test
    void findFollowerList() {
        // given
        Member loginMember = memberRepository.save(createMember("1@email.com"));
        Member follower2 = memberRepository.save(createMember("2@email.com"));
        Member member = memberRepository.save(createMember("3@email.com"));

        memberFollowService.followMember(loginMember.getId(), member.getId());
        memberFollowService.followMember(follower2.getId(), member.getId());
        memberFollowService.followMember(loginMember.getId(), follower2.getId());

        // when
        List<MemberFollowListResponse> followerList = memberFollowService.findFollowerList(member.getId(), loginMember.getId(), 0, 5);

        // then
        Assertions.assertThat(followerList).hasSize(2)
                .extracting("email", "isFollowedByLoginMember", "isLoginMember")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("1@email.com", false, true),
                        Tuple.tuple("2@email.com", true, false)
                );
    }

    @DisplayName("존재하지 않는 회원의 팔로워 목록을 확인하려하면 예외가 발생한다.")
    @Test
    void findFollowerListWithNoExistingMember() {
        // given
        Member loginMember = memberRepository.save(createMember("1@email.com"));
        Member follower2 = memberRepository.save(createMember("2@email.com"));
        Member member = memberRepository.save(createMember("3@email.com"));

        memberFollowService.followMember(loginMember.getId(), member.getId());
        memberFollowService.followMember(follower2.getId(), member.getId());
        memberFollowService.followMember(loginMember.getId(), follower2.getId());

        Long notExistMemberId = Long.MAX_VALUE;

        // when & then
        Assertions.assertThatThrownBy(() -> memberFollowService.findFollowerList(notExistMemberId, loginMember.getId(), 0, 5))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("회원의 팔로잉 목록을 조회한다.")
    @Test
    void findFollowingList() {
        // given
        Member loginMember = memberRepository.save(createMember("1@email.com"));
        Member following2 = memberRepository.save(createMember("2@email.com"));
        Member member = memberRepository.save(createMember("3@email.com"));

        memberFollowService.followMember(member.getId(), loginMember.getId());
        memberFollowService.followMember(member.getId(), following2.getId());
        memberFollowService.followMember(loginMember.getId(), following2.getId());

        // when
        List<MemberFollowListResponse> followerList = memberFollowService.findFollowingList(member.getId(), loginMember.getId(), 0, 5);

        // then
        Assertions.assertThat(followerList).hasSize(2)
                .extracting("email", "isFollowedByLoginMember", "isLoginMember")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("1@email.com", false, true),
                        Tuple.tuple("2@email.com", true, false)
                );
    }

    @DisplayName("존재하지 않는 회원의 팔로잉 목록을 확인하려하면 예외가 발생한다.")
    @Test
    void findFollowingListWithNoExistingMember() {
        // given
        Member loginMember = memberRepository.save(createMember("1@email.com"));
        Member following2 = memberRepository.save(createMember("2@email.com"));
        Member member = memberRepository.save(createMember("3@email.com"));

        memberFollowService.followMember(member.getId(), loginMember.getId());
        memberFollowService.followMember(member.getId(), following2.getId());
        memberFollowService.followMember(loginMember.getId(), following2.getId());

        Long notExistMemberId = Long.MAX_VALUE;

        // when & then
        Assertions.assertThatThrownBy(() -> memberFollowService.findFollowingList(notExistMemberId, loginMember.getId(), 0, 5))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    private Member createMember(String email){
        return Member.builder()
                .email(email)
                .password("test")
                .nickname("test")
                .memberRole(MemberRole.FREELANCER)
                .interestSkillCategoryList(List.of(new SkillCategory(ParentSkillCategory.WEB, ChildSkillCategory.DJANGO)))
                .profileImageName(null)
                .build();
    }
}