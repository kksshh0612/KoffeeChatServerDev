package teamkiim.koffeechat.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.email.service.EmailService;
import teamkiim.koffeechat.domain.file.dto.response.ProfileImageInfoResponse;
import teamkiim.koffeechat.domain.file.service.FileStorageControlService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.member.dto.request.ModifyProfileServiceRequest;
import teamkiim.koffeechat.domain.member.dto.response.MemberInfoResponse;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileStorageControlService fileStorageControlService;
    private final MemberFollowService memberFollowService;
    private final EmailService emailService;

    /**
     * 회원 정보 수정
     *
     * @param modifyProfileServiceRequest 회원 정보 수정 dto
     * @param memberId                    사용자 PK
     * @return ok
     */
    @Transactional
    public void modifyProfile(ModifyProfileServiceRequest modifyProfileServiceRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.modify(modifyProfileServiceRequest.getNickname(), modifyProfileServiceRequest.getMemberRole());
    }

    /**
     * 관심 기술 등록
     *
     * @param memberId                              사용자 PK
     * @param enrollSkillCategoryServiceRequestList 관심 기술 dto
     * @return ok
     */
    @Transactional
    public void enrollSkillCategory(Long memberId, List<EnrollSkillCategoryServiceRequest> enrollSkillCategoryServiceRequestList) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<SkillCategory> skillCategoryList = enrollSkillCategoryServiceRequestList.stream()
                .map(enrollSkillCategoryServiceRequest -> enrollSkillCategoryServiceRequest.combine())
                .collect(Collectors.toList());

        member.enrollSkillCategory(skillCategoryList);
    }

    /**
     * 프로필 이미지 등록
     *
     * @param memberId      회원 PK
     * @param multipartFile 실제 파일
     * @return ok
     */
    @Transactional
    public ProfileImageInfoResponse enrollProfileImage(Long memberId, MultipartFile multipartFile) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ProfileImageInfoResponse response = fileStorageControlService.saveFile(member, multipartFile);

        member.enrollProfileImage(response.getProfileImageName());

        return response;
    }

    /**
     * 회원 프로필 조회
     *
     * @param profileMemberId 조회한 대상 회원의 PK
     * @param loginMemberId   로그인한 회원 (현재 요청을 보낸) 의 PK
     * @return
     */
    public MemberInfoResponse findMemberInfo(Long profileMemberId, Long loginMemberId) {

        boolean isLoginMemberProfile = false;
        Boolean isFollowingMember = null;
        Member profileMember = null;

        // 프로필 PK가 null이면 로그인한 회원 PK로 조회
        if (profileMemberId == null) {
            profileMember = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            isLoginMemberProfile = true;
        } else {
            profileMember = memberRepository.findById(profileMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            isLoginMemberProfile = loginMemberId.equals(profileMember.getId());
        }

        if (!isLoginMemberProfile) {
            Member loginMember = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            isFollowingMember = memberFollowService.isMemberFollowed(loginMember, profileMember);
        }

        return MemberInfoResponse.of(profileMember, isLoginMemberProfile, isFollowingMember);
    }


    /**
     * 사용자 이메일 변경 시 인증 메시지 전송
     */
    @Transactional
    public void sendNewAuthEmail(Long memberId, String email) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getEmail().equals(email) || memberRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        emailService.sendEmailAuthCode(email);
    }

    /**
     * 사용자 이메일 변경
     *
     * @param memberId 로그인한 사용자
     * @param email    변경할 이메일
     */
    @Transactional
    public void updateNewAuthEmail(Long memberId, String email) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateEmail(email);
    }
}
