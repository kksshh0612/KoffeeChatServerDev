package teamkiim.koffeechat.domain.member.service;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.domain.file.dto.response.ProfileImageInfoResponse;
import teamkiim.koffeechat.domain.file.service.FileStorageControlService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.member.dto.request.ModifyProfileServiceRequest;
import teamkiim.koffeechat.domain.member.dto.response.MemberInfoResponse;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.domain.MemberFollow;
import teamkiim.koffeechat.domain.memberfollow.repository.MemberFollowRepository;
import teamkiim.koffeechat.domain.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest extends TestSupport {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberFollowRepository memberFollowRepository;

    @MockBean
    private FileStorageControlService fileStorageControlService;

    @AfterEach
    void tearDown() {
        memberFollowRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 수정 정보를 받아 회원 정보를 수정한다.")
    @Test
    void modifyProfile() {
        // given
        Member saveMember = memberRepository.save(createMember("email@email.com"));

        ModifyProfileServiceRequest modifyProfileServiceRequest = ModifyProfileServiceRequest.builder()
                .nickname("nickname")
                .memberRole(MemberRole.COMPANY_EMPLOYEE_TEMP)
                .build();

        // when
        memberService.modifyProfile(modifyProfileServiceRequest, saveMember.getId());

        // then
        Optional<Member> member = memberRepository.findById(saveMember.getId());            // 수정 후 조회

        Assertions.assertThat(member).isPresent();
        Assertions.assertThat(member.get().getNickname()).isEqualTo(modifyProfileServiceRequest.getNickname());
        Assertions.assertThat(member.get().getMemberRole()).isEqualTo(modifyProfileServiceRequest.getMemberRole());
    }

    @DisplayName("존재하지 않는 회원 id로 회원을 찾아 수정 시도 시 예외가 발생한다.")
    @Test
    void modifyProfileWithNoExistMember() {
        // given
        ModifyProfileServiceRequest modifyProfileServiceRequest = ModifyProfileServiceRequest.builder()
                .nickname("nickname")
                .memberRole(MemberRole.COMPANY_EMPLOYEE_TEMP)
                .build();

        Long notExistMemberId = Long.MAX_VALUE; // 존재하지 않는 ID

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.modifyProfile(modifyProfileServiceRequest, notExistMemberId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("회원의 관심 기술을 등록한다.")
    @Test
    void enrollSkillCategory() {
        // given
        Member saveMember = memberRepository.save(createMember("email@email.com"));

        EnrollSkillCategoryServiceRequest request1 = EnrollSkillCategoryServiceRequest.builder()
                .parentSkillCategory(ParentSkillCategory.DATABASE)
                .childSkillCategory(ChildSkillCategory.CASSANDRA)
                .build();
        EnrollSkillCategoryServiceRequest request2 = EnrollSkillCategoryServiceRequest.builder()
                .parentSkillCategory(ParentSkillCategory.WEB)
                .childSkillCategory(ChildSkillCategory.SPRING_BOOT)
                .build();

        List<EnrollSkillCategoryServiceRequest> skillCategoryServiceRequests = List.of(request1, request2);

        // when
        memberService.enrollSkillCategory(saveMember.getId(), skillCategoryServiceRequests);

        // then
        Optional<Member> member = memberRepository.findByIdWithSkillCategories(saveMember.getId());            // 수정 후 조회

        Assertions.assertThat(member).isPresent();
        Assertions.assertThat(member.get().getInterestSkillCategoryList()).hasSize(2)
                .extracting("parentSkillCategory", "childSkillCategory")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(ParentSkillCategory.DATABASE, ChildSkillCategory.CASSANDRA),
                        Tuple.tuple(ParentSkillCategory.WEB, ChildSkillCategory.SPRING_BOOT)
                );
    }

    @DisplayName("존재하지 않는 회원 id로 회원을 찾아 회원의 관심 기술을 등록 시도할 시, 에러가 발생한다.")
    @Test
    void enrollSkillCategoryWithNoExistMember() {
        // given
        EnrollSkillCategoryServiceRequest request1 = EnrollSkillCategoryServiceRequest.builder()
                .parentSkillCategory(ParentSkillCategory.DATABASE)
                .childSkillCategory(ChildSkillCategory.CASSANDRA)
                .build();
        EnrollSkillCategoryServiceRequest request2 = EnrollSkillCategoryServiceRequest.builder()
                .parentSkillCategory(ParentSkillCategory.WEB)
                .childSkillCategory(ChildSkillCategory.SPRING_BOOT)
                .build();

        List<EnrollSkillCategoryServiceRequest> skillCategoryServiceRequests = List.of(request1, request2);

        Long notExistMemberId = Long.MAX_VALUE; // 존재하지 않는 ID

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.enrollSkillCategory(notExistMemberId, skillCategoryServiceRequests))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

   @DisplayName("회원 프로필 사진을 등록하고 저장된 파일 이름을 반환한다.")
   @Test
   void enrollProfileImage() {
       // given
       Member saveMember = memberRepository.save(createMember("email@email.com"));
       MultipartFile multipartFile = new MockMultipartFile(
               "file",
               "filename.jpg",
               "image/jpeg",
               "file content".getBytes()
       );

       ProfileImageInfoResponse profileImageInfoResponse = ProfileImageInfoResponse.builder()
               .profileImagePath("path")
               .profileImageName("name")
               .build();

       BDDMockito.given(fileStorageControlService.saveFile(any(Member.class), any(MultipartFile.class)))
               .willReturn(profileImageInfoResponse);

       // when
       ProfileImageInfoResponse response = memberService.enrollProfileImage(saveMember.getId(), multipartFile);

       // then
       Assertions.assertThat(response.getProfileImagePath()).isEqualTo("path");
       Assertions.assertThat(response.getProfileImageName()).isEqualTo("name");
   }

    @DisplayName("존재하지 않는 회원 프로필 사진을 등록하려 하면 예외가 발생한다.")
    @Test
    void enrollProfileImageWithNoExistMember() {
        // given
        Long notExistMemberId = Long.MAX_VALUE;
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "filename.jpg",
                "image/jpeg",
                "file content".getBytes()
        );

        ProfileImageInfoResponse profileImageInfoResponse = ProfileImageInfoResponse.builder()
                .profileImagePath("path")
                .profileImageName("name")
                .build();

        BDDMockito.given(fileStorageControlService.saveFile(any(Member.class), any(MultipartFile.class)))
                .willReturn(profileImageInfoResponse);

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.enrollProfileImage(notExistMemberId, multipartFile))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("현재 로그인한 멤버가 팔로우한 멤버의 프로필을 조회한다.")
    @Test
    void findOtherFollowedMemberInfo() {
        // given
        Member profileMember = memberRepository.save(createMember("1@email.com"));
        Member loginMember = memberRepository.save(createMember("2@email.com"));
        memberFollowRepository.save(new MemberFollow(loginMember, profileMember));

        // when
        MemberInfoResponse memberInfoResponse = memberService.findMemberInfo(profileMember.getId(), loginMember.getId());

        // then
        Assertions.assertThat(memberInfoResponse).isNotNull();
        Assertions.assertThat(memberInfoResponse.getMemberId()).isEqualTo(profileMember.getId());
        Assertions.assertThat(memberInfoResponse.getIsLoginMember()).isEqualTo(false);
        Assertions.assertThat(memberInfoResponse.getIsFollowingMember()).isEqualTo(true);
    }

    @DisplayName("현재 로그인한 멤버가 팔로우하지 않은 멤버의 프로필을 조회한다.")
    @Test
    void findOtherNotFollowedMemberInfo() {
        // given
        Member profileMember = memberRepository.save(createMember("1@email.com"));
        Member loginMember = memberRepository.save(createMember("2@email.com"));

        // when
        MemberInfoResponse memberInfoResponse = memberService.findMemberInfo(profileMember.getId(), loginMember.getId());

        // then
        Assertions.assertThat(memberInfoResponse).isNotNull();
        Assertions.assertThat(memberInfoResponse.getMemberId()).isEqualTo(profileMember.getId());
        Assertions.assertThat(memberInfoResponse.getIsLoginMember()).isEqualTo(false);
        Assertions.assertThat(memberInfoResponse.getIsFollowingMember()).isEqualTo(false);
    }

    @DisplayName("현재 로그인한 멤버가 자신의 프로필을 조회한다.")
    @Test
    void findMyMemberInfo() {
        // given
        Member loginMember = memberRepository.save(createMember("2@email.com"));

        // when
        MemberInfoResponse memberInfoResponse = memberService.findMemberInfo(loginMember.getId(), loginMember.getId());

        // then
        Assertions.assertThat(memberInfoResponse).isNotNull();
        Assertions.assertThat(memberInfoResponse.getMemberId()).isEqualTo(loginMember.getId());
        Assertions.assertThat(memberInfoResponse.getIsLoginMember()).isEqualTo(true);
        Assertions.assertThat(memberInfoResponse.getIsFollowingMember()).isNull();
    }

    @DisplayName("존재하지 않는 회원의 프로필을 조회하면 예외가 발생한다.")
    @Test
    void findMemberInfoWithNoExistMember() {
        // given
        Member loginMember = memberRepository.save(createMember("2@email.com"));
        Long notExistMemberId = Long.MAX_VALUE;

        // when & then
        Assertions.assertThatThrownBy(() -> memberService.findMemberInfo(notExistMemberId, loginMember.getId()))
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