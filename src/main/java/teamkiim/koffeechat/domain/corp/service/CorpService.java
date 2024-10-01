package teamkiim.koffeechat.domain.corp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.domain.WaitingCorp;
import teamkiim.koffeechat.domain.corp.dto.response.CorpDomainResponse;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.domain.corp.repository.WaitingCorpRepository;
import teamkiim.koffeechat.domain.email.domain.EmailAuth;
import teamkiim.koffeechat.domain.email.repository.EmailAuthRepository;
import teamkiim.koffeechat.domain.email.service.EmailSendService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpService {

    private final MemberRepository memberRepository;
    private final CorpRepository corpRepository;
    private final WaitingCorpRepository waitingCorpRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailSendService emailSendService;

    /**
     * 회사 도메인 등록 요청 대기 상태로 저장
     * 거절된 도메인에 대해 요청하면 -> 요청 거절
     */
    @Transactional
    public String createWaitingCorp(Long memberId, String corpName, String corpDomain) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<Corp> corp = corpRepository.findByNameAndEmailDomain(corpName, corpDomain);

        if (corp.isPresent()) {  // 이미 있는 요청
            if (corp.get().getVerified().equals(Verified.REJECTED)) {
                throw new CustomException(ErrorCode.CORP_DOMAIN_FORBIDDEN);
            }
            if (corp.get().getVerified().equals(Verified.APPROVED)) {
                throw new CustomException(ErrorCode.CORP_ALREADY_EXIST);
            }
            if (corp.get().getVerified().equals(Verified.WAITING)) {
                if (waitingCorpRepository.existsByMemberAndCorp(member, corp.get())) {  //이미 존재하는 요청인 경우
                    throw new CustomException(ErrorCode.CORP_REQUEST_ALREADY_EXIST);
                }
                waitingCorpRepository.save(new WaitingCorp(member, corp.get()));  // 대기 요청 저장
            }

        } else {  // 새로운 요청 -> 요청 생성
            Corp savedCorp = corpRepository.save(new Corp(corpName, corpDomain, Verified.WAITING));
            waitingCorpRepository.save(new WaitingCorp(member, savedCorp));  // 대기 요청 저장
        }

        return "도메인 등록이 요청되었습니다. 승인이 될 때까지 기다려 주세요.";
    }

    /**
     * 회사 도메인 검색 : 회사 검색, 도메인 검색
     *
     * @param corpName 회사 이름
     */
    public List<CorpDomainResponse> findCorpDomain(String corpName) {

        List<Corp> corpList = corpRepository.findApprovedCorpByName(corpName, Verified.APPROVED);

        return corpList.stream().map(CorpDomainResponse::of).toList();
    }

    /**
     * @param corpDomain 회사 도메인
     */
    public List<CorpDomainResponse> findCorpName(String corpDomain) {

        List<Corp> corpList = corpRepository.findApprovedCorpByDomain(corpDomain, Verified.APPROVED);

        return corpList.stream().map(CorpDomainResponse::of).toList();
    }

    /**
     * 현직자 인증 시 이메일 전송
     *
     * @param corpName 회사 이름
     * @param email    회사 이메일
     * @param memberId 요청한 회원 pk
     * @return 인증 이메일 전송 상태 메시지
     */
    @Transactional
    public String createEmailAuth(String corpName, String email, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String domain = email.substring(email.indexOf('@') + 1);
        Optional<Corp> corp = corpRepository.findByEmailDomain(domain);

        validateDomain(corp, corpName, domain);  //유효한 이메일인지 확인

        //인증된 도메인인 경우 메일 발송
        EmailAuth emailAuth = new EmailAuth(email);
        emailAuthRepository.save(emailAuth);

        emailSendService.sendCorpEmail(member, emailAuth);

        return "이메일 전송 완료, 이메일을 확인해주세요";
    }

    /**
     * 유효한 이메일인지 확인
     */
    private void validateDomain(Optional<Corp> corp, String corpName, String domain) {
        if (corp.isEmpty()) {
            corpRepository.save(new Corp(corpName, domain, Verified.WAITING));  //도메인 승인 요청 생성
            throw new CustomException(ErrorCode.CORP_DOMAIN_WAITING);
        }

        if (corp.get().getVerified().equals(Verified.WAITING)) {
            throw new CustomException(ErrorCode.CORP_DOMAIN_WAITING);
        }

        if (corp.get().getVerified().equals(Verified.REJECTED)) {
            throw new CustomException(ErrorCode.CORP_DOMAIN_FORBIDDEN);
        }

    }

    /**
     * 현직자 인증 시 이메일 인증코드 확인
     *
     * @param email 회원의 회사 이메일
     * @param code  회사 이메일 인증 코드
     */
    @Transactional
    public void checkEmailAuthCode(Long memberId, String corpName, String email, String code) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        EmailAuth emailAuth = emailAuthRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_NOT_MATCH));

        emailAuthRepository.delete(emailAuth);

        member.authCorp(corpName, email);  // 회원의 회사 정보 업데이트
    }
}
