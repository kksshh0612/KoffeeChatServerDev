package teamkiim.koffeechat.domain.file.service.s3;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Profile("prod")
@Slf4j
public class S3ProfileFileService implements ProfileFileService {

    private final MemberRepository memberRepository;
    private final S3FileStorageControlService s3FileStorageControlService;

    @Value("${domain-name}")
    private String domainName;

    private static String URL_PREFIX = "https://";

    /**
     * 프로필 이미지 등록
     *
     * @param multipartFile null
     * @param memberId      등록하는 회원 PK
     * @return ImagePathResponse
     */
    @Override
    @Transactional
    public ImageUrlResponse uploadProfileImage(MultipartFile multipartFile, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String fileName = UUID.randomUUID().toString();

        // 이미 등록된 프로필 이미지 있으면 삭제
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl()
                .equals("https://koffeechat.site/basic_profile_image.png")) {
            s3FileStorageControlService.deleteFile(member.getProfileImageUrl());
        }

        String presignedUrl = s3FileStorageControlService.createPresignedUrl(fileName);

        String saveUrl = URL_PREFIX + domainName + java.io.File.separator + fileName;

        member.enrollProfileImage(saveUrl);

        return ImageUrlResponse.of(presignedUrl, fileName, saveUrl);
    }
}
