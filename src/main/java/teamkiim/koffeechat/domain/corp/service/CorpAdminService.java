package teamkiim.koffeechat.domain.corp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.corp.domain.Corp;
import teamkiim.koffeechat.domain.corp.domain.Verified;
import teamkiim.koffeechat.domain.corp.repository.CorpRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorpAdminService {

    private final CorpRepository corpRepository;

    @Transactional
    public void saveCorpDomain(String corpName, String corpDomain) {
        Optional<Corp> corp = corpRepository.findByCorpNameAndCorpEmailDomain(corpName, corpDomain);
        if (corp.isPresent()) {
            throw new CustomException(ErrorCode.CORP_ALREADY_EXIST);
        }
        Corp corpRequest = new Corp(corpName, corpDomain, Verified.APPROVED);
        corpRepository.save(corpRequest);
    }

    @Transactional
    public Verified modifyCorpDomain(String corpName, String corpEmailDomain, Verified verified) {
        Corp corp = corpRepository.findByCorpNameAndCorpEmailDomain(corpName, corpEmailDomain)
                .orElseThrow(()-> new CustomException(ErrorCode.CORP_NOT_FOUND));

        corp.statusModify(verified);

        return corp.getVerified();
    }
}
