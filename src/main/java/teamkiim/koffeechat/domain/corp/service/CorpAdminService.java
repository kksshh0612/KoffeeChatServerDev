package teamkiim.koffeechat.domain.corp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.corp.controller.dto.response.AdminCorpDomainListResponse;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.domain.WaitingCorp;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.domain.corp.repository.WaitingCorpRepository;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.notification.service.NotificationService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpAdminService {

    private final CorpRepository corpRepository;
    private final WaitingCorpRepository waitingCorpRepository;
    private final NotificationService notificationService;

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

        corpRepository.save(new Corp(corpName, corpDomain, Verified.APPROVED));
    }

    /**
     * 관리자가 회원이 승인 요청한 도메인들에 대해 상태 변경
     *
     * @param corpId   등록된 회사 pk
     * @param verified 인증 상태 변경 WAITING -> APPROVED|REJECTED
     * @return verified  변경된 인증 상태
     */
    @Transactional
    public Verified updateCorpVerified(Long corpId, Verified verified) {
        Corp corp = corpRepository.findById(corpId)
                .orElseThrow(() -> new CustomException(ErrorCode.CORP_NOT_FOUND));

        corp.statusModify(verified);

        List<WaitingCorp> waitingCorpList = waitingCorpRepository.findByCorp(corp);
        List<Member> memberList = waitingCorpList.stream().map(WaitingCorp::getMember).toList();
        notificationService.createCorpNotification(memberList, corp, verified);  //알림 전송
        waitingCorpRepository.deleteAll(waitingCorpList);

        return corp.getVerified();
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

    /**
     * 전체 도메인 리스트 확인
     *
     * @return List<AdminCorpDomainListResponse> 도메인 리스트 response
     */
    public List<AdminCorpDomainListResponse> listCorp() {
        List<Corp> corpList = corpRepository.findAll();

        return corpList.stream().map(AdminCorpDomainListResponse::of).toList();
    }

    /**
     * 도메인 키워드로 검색
     *
     * @return List<AdminCorpDomainListResponse> 도메인 리스트 response
     */
    public List<AdminCorpDomainListResponse> findCorpByKeyword(String keyword) {
        List<Corp> corpList = corpRepository.findByKeyword(keyword);

        return corpList.stream().map(AdminCorpDomainListResponse::of).toList();
    }
}
