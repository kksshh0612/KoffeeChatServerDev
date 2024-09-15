package teamkiim.koffeechat.domain.corp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.domain.corp.dto.response.CorpDomainResponse;
import teamkiim.koffeechat.domain.email.domain.EmailAuth;
import teamkiim.koffeechat.domain.email.repository.EmailAuthRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpService {

    private final MemberRepository memberRepository;
    private final CorpRepository corpRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final CorpEmailSendService corpEmailSendService;

    /**
     * 회사 도메인 등록 요청 대기 상태로 저장
     * 거절된 도메인에 대해 요청하면 -> 요청 거절
     */
    @Transactional
    public String saveCorpRequest(String corpName, String corpDomain) {

        Optional<Corp> corp = corpRepository.findByCorpNameAndCorpEmailDomain(corpName, corpDomain);

        if (corp.isPresent()) {  // 이미 있는 요청
            if (corp.get().getVerified().equals(Verified.REJECTED)) return "승인 거절된 도메인입니다. 이메일 확인 후 문의해주세요.";
            if (corp.get().getVerified().equals(Verified.APPROVED)) return "이미 존재하는 도메인입니다.";
        } else {                 // 새로운 요청 -> 요청 생성
            Corp corpRequest = new Corp(corpName, corpDomain, Verified.WAITING);
            corpRepository.save(corpRequest);
        }

        return "도메인 등록이 요청되었습니다. 승인이 될 때까지 기다려 주세요.";
    }

    /**
     * 회사 도메인 검색 : 회사 검색, 도메인 검색
     *
     * @param corpName 회사 이름
     */
    public List<CorpDomainResponse> findCorpRequest(String corpName) {

        List<Corp> corpList = corpRepository.findAllByCorpNameContainingIgnoreCaseAndVerified(corpName, Verified.APPROVED);

        List<CorpDomainResponse> responseList = corpList.stream().map(CorpDomainResponse::of).collect(Collectors.toList());
        return responseList;
    }

    /**
     * @param corpDomain 회사 도메인
     */
    public List<CorpDomainResponse> findCorpNameRequest(String corpDomain) {

        List<Corp> corpList = corpRepository.findAllByCorpEmailDomainContainingIgnoreCaseAndVerified(corpDomain, Verified.APPROVED);

        List<CorpDomainResponse> responseList = corpList.stream().map(CorpDomainResponse::of).collect(Collectors.toList());

        return responseList;
    }

    /**
     * 현직자 인증 시 이메일 전송
     *
     * @param email
     * @param memberId
     */
    @Transactional
    public String sendCorpEmailAuthCode(String corpName, String email, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String domain = email.substring(email.indexOf('@') + 1);
        Optional<Corp> corp = corpRepository.findByCorpEmailDomain(domain);
        if (corp.isEmpty()) {
            Corp newCorp = new Corp(corpName, domain, Verified.WAITING);
            corpRepository.save(newCorp);
            return "등록되어있지 않은 회사입니다. 도메인 승인이 되면 다시 인증해주세요.";
        }

        if (corp.get().getVerified().equals(Verified.WAITING)) {
            return "등록 요청이 되어있는 도메인입니다. 도메인 승인이 되면 다시 인증해주세요.";
        }
        if (corp.get().getVerified().equals(Verified.REJECTED)) {
            return "인증할 수 없는 도메인입니다. 이메일을 확인 후 문의해주세요.";
        }

        //인증된 도메인인 경우 메일 발송
        EmailAuth emailAuth = new EmailAuth(email);
        emailAuthRepository.save(emailAuth);

        corpEmailSendService.sendEmail(member, emailAuth);

        return "이메일 전송 완료, 이메일을 확인해주세요";
    }

    /**
     * 현직자 인증 시 이메일 인증코드 확인
     *
     * @param email 회원의 회사 이메일
     * @param code  회사 이메일 인증 코드
     */
    @Transactional
    public String checkCorpEmailAuthCode(Long memberId, String corpName, String email, String code) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        EmailAuth emailAuth = emailAuthRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_NOT_MATCH));

        emailAuthRepository.delete(emailAuth);

        member.authCorp(corpName, email);

        return "인증 되었습니다.";
    }
}
