package teamkiim.koffeechat.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.dto.request.EnrollSkillCategoryServiceRequest;
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
}
