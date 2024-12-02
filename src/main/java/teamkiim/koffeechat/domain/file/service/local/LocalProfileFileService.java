package teamkiim.koffeechat.domain.file.service.local;

import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.service.ProfileFileService;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile("local")
public class LocalProfileFileService implements ProfileFileService {

    private final MemberRepository memberRepository;
    private final LocalFileStorageControlService localFileStorageControlService;

    @Value("${file-path}")
    private String baseFilePath;

    private static final String profileImagePath = "PROFILE";
    private static final String basicProfileImageName = "basic_profile_image.png";

    /**
     * 프로필 이미지 등록
     *
     * @param multipartFile 등록할 파일
     * @param memberId      등록할 회원 PK
     * @return ImageUrlResponse
     */
    @Override
    @Transactional
    public ImageUrlResponse uploadProfileImage(MultipartFile multipartFile, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String saveFileUrl = Paths.get(baseFilePath, profileImagePath,
                UUID.randomUUID() + "_" + multipartFile.getOriginalFilename()).toString();

        // 이미 등록된 프로필 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().equals(basicProfileImageName)) {
            localFileStorageControlService.deleteFile(saveFileUrl);
        }

        localFileStorageControlService.uploadFile(saveFileUrl, multipartFile);

        member.enrollProfileImage(saveFileUrl);

        return ImageUrlResponse.of(null, null, saveFileUrl);
    }
}
