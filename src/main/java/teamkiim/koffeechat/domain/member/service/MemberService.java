package teamkiim.koffeechat.domain.member.service;

import java.io.File;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.email.service.EmailService;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.service.FileStorageService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.domain.member.dto.request.ModifyProfileServiceRequest;
import teamkiim.koffeechat.domain.member.dto.request.UpdatePasswordRequest;
import teamkiim.koffeechat.domain.member.dto.response.MemberInfoResponse;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.memberfollow.service.MemberFollowService;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;
    private final MemberFollowService memberFollowService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final AESCipherUtil aesCipherUtil;

    @Value("${file-path}")
    private String baseFilePath;

    private static final String profileImagePath = "PROFILE";
    private static final String basicProfileImageName = "basic_profile_image.png";

    /**
     * 사용자 닉네임으로 암호화된 pk 요청
     */
    public String getMemberPK(String memberEmailId) {
        Member member = memberRepository.findByEmailId(memberEmailId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return aesCipherUtil.encrypt(member.getId());
    }

    /**
     * 회원 정보 수정
     *
     * @param modifyProfileServiceRequest 회원 정보 수정 dto
     * @param memberId                    사용자 PK
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
     */
    @Transactional
    public void enrollSkillCategory(Long memberId,
                                    List<EnrollSkillCategoryServiceRequest> enrollSkillCategoryServiceRequestList) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<SkillCategory> skillCategoryList = enrollSkillCategoryServiceRequestList.stream()
                .map(EnrollSkillCategoryServiceRequest::combine).toList();

        member.enrollSkillCategory(skillCategoryList);
    }

    /**
     * 프로필 이미지 로컬 파일시스템에 등록
     *
     * @param memberId      회원 PK
     * @param multipartFile 실제 파일
     * @return ImageUrlResponse
     */
    @Transactional
    public ImageUrlResponse enrollProfileImageToLocal(Long memberId, MultipartFile multipartFile) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String saveFileUrl = new StringBuilder(baseFilePath)
                .append(File.separator)
                .append(profileImagePath)
                .append(File.separator)
                .append(UUID.randomUUID())
                .append("_")
                .append(multipartFile.getOriginalFilename())
                .toString();

        // 이미 등록된 프로필 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().equals(basicProfileImageName)) {
            fileStorageService.deleteFile(saveFileUrl);
        }

        fileStorageService.uploadFile(saveFileUrl, multipartFile);

        member.enrollProfileImage(saveFileUrl);

        return ImageUrlResponse.of(saveFileUrl);
    }

    /**
     * 프로필 이미지 S3에 등록
     *
     * @param memberId      회원 PK
     * @param multipartFile 실제 파일
     * @return ImageUrlResponse
     */
    @Transactional
    public ImageUrlResponse enrollProfileImageToS3(Long memberId, MultipartFile multipartFile) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        // 이미 등록된 프로필 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().equals("koffeechat.site/basic_profile_image.png")) {
            fileStorageService.deleteFile(member.getProfileImageUrl());
        }

        ImageUrlResponse imageUrlResponse = fileStorageService.uploadFile(fileName, multipartFile);

        member.enrollProfileImage(imageUrlResponse.getUrl());

        return imageUrlResponse;
    }

    /**
     * 회원 프로필 조회
     *
     * @param profileMemberId 조회한 대상 회원의 암호화된 PK
     * @param loginMemberId   로그인한 회원 (현재 요청을 보낸) 의 PK
     * @return MemberInfoResponse
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

        boolean isCorpVerified = profileMember.getCorpName() != null;  // 현직자 인증 여부

        return MemberInfoResponse.of(aesCipherUtil.encrypt(profileMember.getId()), profileMember, isLoginMemberProfile,
                isFollowingMember, isCorpVerified);
    }


    /**
     * 사용자 이메일 변경 시 인증 메시지 전송
     */
    @Transactional
    public void sendNewAuthEmail(Long memberId, String email) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateEmail(email);
    }

    /**
     * 기존 비밀번호 확인
     *
     * @param memberId 로그인한 사용자
     * @param password 현재 비밀번호
     */
    public void checkCurrentPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 비밀번호 변경
     *
     * @param memberId        로그인한 사용자 PK
     * @param passwordRequest 비밀번호 변경 요청
     */
    @Transactional
    public void updatePassword(Long memberId, UpdatePasswordRequest passwordRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //기존 비밀번호와 동일한 비밀번호로 변경 요청
        if (passwordEncoder.matches(passwordRequest.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_EQUAL);
        }

        //비밀번호 확인 실패
        if (!passwordRequest.getNewPassword().equals(passwordRequest.getCheckPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        member.encodePassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }
}
