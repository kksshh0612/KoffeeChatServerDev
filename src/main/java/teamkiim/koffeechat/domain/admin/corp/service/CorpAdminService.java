package teamkiim.koffeechat.domain.admin.corp.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.admin.corp.domain.VerifyStatus;
import teamkiim.koffeechat.domain.admin.corp.dto.request.UpdateCorpDomainStatusServiceRequest;
import teamkiim.koffeechat.domain.admin.corp.dto.response.AdminCorpDomainListResponse;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.WaitingCorp;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.domain.corp.repository.WaitingCorpRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpAdminService {

    private final CorpRepository corpRepository;
    private final WaitingCorpRepository waitingCorpRepository;
    private final NotificationService notificationService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 관리자가 인증된 회사 도메인 등록
     *
     * @param corpName   회사
     * @param corpDomain 회사 도메인
     */
    @Transactional
    public void createApprovedCorp(String corpName, String corpDomain) {

        Optional<Corp> corp = corpRepository.findByNameAndEmailDomain(corpName, corpDomain);
        if (corp.isPresent()) {
            throw new CustomException(ErrorCode.CORP_ALREADY_EXIST);
        }

        corpRepository.save(new Corp(corpName, corpDomain, VerifyStatus.APPROVED));
    }

    /**
     * 관리자가 회원이 요청한 회사 도메인 승인/거절
     *
     * @param corpId 회사 도메인 PK
     */
    @Transactional
    public void updateCorpDoaminStatus(Long corpId,
                                       UpdateCorpDomainStatusServiceRequest updateCorpDomainStatusServiceRequest) {

        VerifyStatus verifyStatus = updateCorpDomainStatusServiceRequest.getVerifyStatus();
        Corp corp = corpRepository.findById(corpId)
                .orElseThrow(() -> new CustomException(ErrorCode.CORP_NOT_FOUND));

        corp.changeVerifyStatus(verifyStatus);

        List<WaitingCorp> waitingCorpList = waitingCorpRepository.findByCorp(corp);
        List<Member> memberList = waitingCorpList.stream()
                .map(WaitingCorp::getMember).toList();
        notificationService.createCorpNotification(memberList, corp, verifyStatus);  //알림 전송
        waitingCorpRepository.deleteAll(waitingCorpList);
    }

    /**
     * 도메인 키워드로 검색
     *
     * @param keyword 검색 키워드
     * @return List<AdminCorpDomainListResponse>
     */
    public List<AdminCorpDomainListResponse> findCorpListByKeyword(String keyword) {

        List<Corp> corpList = corpRepository.findAllByKeyword(keyword);

        return corpList.stream()
                .map(corp -> AdminCorpDomainListResponse.of(aesCipherUtil.encrypt(corp.getId()), corp))
                .toList();
    }

    /**
     * 회사 도메인 삭제
     *
     * @param corpId
     */
    @Transactional
    public void deleteCorp(Long corpId) {
        Corp corp = corpRepository.findById(corpId)
                .orElseThrow(() -> new CustomException(ErrorCode.CORP_NOT_FOUND));

        corpRepository.delete(corp);
    }
}
