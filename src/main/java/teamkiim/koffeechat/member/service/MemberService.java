package teamkiim.koffeechat.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.file.dto.response.ProfileImageInfoResponse;
import teamkiim.koffeechat.file.service.FileStorageControlService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.dto.request.EnrollSkillCategoryServiceRequest;
import teamkiim.koffeechat.member.dto.request.ModifyProfileServiceRequest;
import teamkiim.koffeechat.member.dto.response.MemberInfoResponse;
import teamkiim.koffeechat.member.repository.MemberRepository;
import teamkiim.koffeechat.post.dev.domain.SkillCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileStorageControlService fileStorageControlService;

    @Transactional
    public ResponseEntity<?> modifyProfile(ModifyProfileServiceRequest modifyProfileServiceRequest, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.modify(modifyProfileServiceRequest.getNickname(), modifyProfileServiceRequest.getMemberRole());

        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    /**
     * 관심 기술 등록
     * @param memberId 사용자 PK
     * @param enrollSkillCategoryServiceRequestList 관심 기술 dto
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> enrollSkillCategory(Long memberId,
                                                 List<EnrollSkillCategoryServiceRequest> enrollSkillCategoryServiceRequestList){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<SkillCategory> skillCategoryList = enrollSkillCategoryServiceRequestList.stream()
                .map(enrollSkillCategoryServiceRequest -> enrollSkillCategoryServiceRequest.combine())
                        .collect(Collectors.toList());

        member.enrollSkillCategory(skillCategoryList);

        return ResponseEntity.ok("관심 기술 설정 완료");
    }

    /**
     * 프로필 이미지 등록
     * @param memberId 회원 PK
     * @param multipartFile 실제 파일
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> enrollProfileImage(Long memberId, MultipartFile multipartFile){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ProfileImageInfoResponse response = fileStorageControlService.saveFile(member, multipartFile);

        member.enrollProfileImage(response.getProfileImageName());

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 프로필 조회
     * @param profileMemberId 조회한 대상 회원의 PK
     * @param loginMemberId 로그인한 회원 (현재 요청을 보낸) 의 PK
     * @return
     */
    public ResponseEntity<?> findMemberInfo(Long profileMemberId, Long loginMemberId){

        Member member;
        boolean isLoginMemberInfo;

        if(profileMemberId == null){
            member = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            isLoginMemberInfo = true;
        }
        else{
            member = memberRepository.findById(profileMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            isLoginMemberInfo = (member.getId() == loginMemberId) ? true : false;
        }

        MemberInfoResponse response = MemberInfoResponse.of(member, isLoginMemberInfo);

        return ResponseEntity.ok(response);
    }
}
