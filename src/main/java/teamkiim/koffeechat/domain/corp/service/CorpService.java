package teamkiim.koffeechat.domain.corp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.domain.corp.service.dto.response.CorpDomainResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpService {

    //corpRepository에 저장되어있는 회사 도메인으로 인증 메일 보내기
    private final CorpRepository corpRepository;

    /**
     * 회사 도메인 인증 승인 요청 대기 상태로 저장
     * 거절된 도메인에 대해 요청하면 -> 요청 거절
     */
    @Transactional
    public String saveCorpRequest(String corpName , String corpDomain){

        Optional<Corp> corp = corpRepository.findByCorpNameAndCorpEmailDomain(corpName, corpDomain);

        if (corp.isPresent()) {  // 이미 있는 요청
            if(corp.get().getVerified()==Verified.REJECTED) return "승인 거절된 도메인입니다.";
        }else{                   // 새로운 요청 -> 요청 생성
            Corp corpRequest = new Corp(corpName, corpDomain, Verified.WAITING);
            corpRepository.save(corpRequest);
        }

        return "도메인 검증이 요청되었습니다. 승인이 될 때까지 기다려 주세요.";
    }

    /**
     * 회사 도메인 검색 : 회사 검색, 도메인 검색
     * @param corpName 회사 이름
     */
    public List<CorpDomainResponse> findCorpRequest(String corpName) {

        List<Corp> corpList=corpRepository.findAllByCorpNameContainingIgnoreCaseAndVerified(corpName, Verified.APPROVED);

        List<CorpDomainResponse> responseList = corpList.stream().map(CorpDomainResponse::of).collect(Collectors.toList());
        return responseList;
    }

    /**
     * @param corpDomain 회사 도메인
     */
    public List<CorpDomainResponse> findCorpNameRequest(String corpDomain) {

        List<Corp> corpList=corpRepository.findAllByCorpEmailDomainContainingIgnoreCaseAndVerified(corpDomain, Verified.APPROVED);

        List<CorpDomainResponse> responseList = corpList.stream().map(CorpDomainResponse::of).collect(Collectors.toList());

        return responseList;
    }
}
