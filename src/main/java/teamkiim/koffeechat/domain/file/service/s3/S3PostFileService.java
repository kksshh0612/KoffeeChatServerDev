package teamkiim.koffeechat.domain.file.service.s3;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.domain.PostFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.repository.FileRepository;
import teamkiim.koffeechat.domain.file.repository.PostFileRepository;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class S3PostFileService implements PostFileService {

    private final FileRepository fileRepository;
    private final PostFileRepository postFileRepository;
    private final PostRepository postRepository;
    private final S3FileStorageControlService s3FileStorageControlService;

    @Value("${domain-name}")
    private String domainName;

    private static String URL_PREFIX = "https://";

    /**
     * 이미지 파일 S3에 단건 저장
     *
     * @param multipartFile null
     * @param postId        연관 게시물 PK
     * @return ImagePathResponse
     */
    @Override
    @Transactional
    public ImageUrlResponse uploadImageFile(MultipartFile multipartFile, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        String fileName = UUID.randomUUID().toString();

        String presignedUrl = s3FileStorageControlService.createPresignedUrl(fileName);

        String saveUrl = URL_PREFIX + domainName + java.io.File.separator + fileName;

        PostFile saveFile = postFileRepository.save(new PostFile(saveUrl, post));
        post.addPostFile(saveFile);                         // 양방향 연관관계 주입

        return ImageUrlResponse.of(presignedUrl, fileName, saveUrl);
    }

    /**
     * 게시물에 연관된 이미지 파일 모두 삭제 (게시글 작성 취소 시 호출)
     *
     * @param post 연관 게시물
     */
    @Override
    @Transactional
    public void deleteImageFiles(Post post) {

        List<PostFile> deleteFileList = postFileRepository.findAllByPost(post);

        List<String> urls = deleteFileList.stream()
                .map(PostFile::getUrl)
                .collect(Collectors.toList());

        s3FileStorageControlService.deleteFiles(urls);

        fileRepository.deleteAll(deleteFileList);
    }

    /**
     * 이미지 파일 다건 삭제 (post에 연관된 File 중 id값이 fileIdList에 없는 File 삭제)
     *
     * @param fileUrlList 삭제하지 않을 이미지 파일 id 리스트
     * @param post        연관 게시물
     */
    @Override
    @Transactional
    public void deleteImageFiles(List<String> fileUrlList, Post post) {

        List<PostFile> existFiles = postFileRepository.findAllByPost(post);

        List<File> deleteFiles = existFiles.stream()
                .filter(file -> !fileUrlList.contains(file.getUrl()))
                .collect(Collectors.toList());

        List<String> deleteFileUrls = deleteFiles.stream()
                .map(File::getUrl)
                .collect(Collectors.toList());

        log.info("[S3PostFileService / deleteImageFiles] 삭제할 파일 수 : {}", deleteFileUrls.size());
        for (String url : deleteFileUrls) {
            log.info("deleteFileUrl : {}", url);
        }

        if (deleteFileUrls.isEmpty()) {
            return;
        }

        s3FileStorageControlService.deleteFiles(deleteFileUrls);

        fileRepository.deleteAll(deleteFiles);
    }
}
