package teamkiim.koffeechat.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.domain.file.repository.FileRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final FileStorageControlService fileStorageControlService;

    /**
     * 이미지 파일 단건 저장
     * @param multipartFile 실제 파일
     * @param postId 연관 게시물 PK
     * @return ImagePathResponse
     */
    @Transactional
    public ResponseEntity<?> saveImageFile(MultipartFile multipartFile, Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        File file = new File(post, multipartFile);

        fileStorageControlService.saveFile(file, multipartFile);

        File saveFile = fileRepository.save(file);

        post.addFile(saveFile);                         // 양방향 연관관계 주입

        return ResponseEntity.ok(ImagePathResponse.of(saveFile));
    }

    /**
     * 게시물에 연관된 이미지 파일 모두 삭제 (게시글 작성 취소 시 호출)
     * @param post 연관 게시물
     */
    @Transactional
    public void deleteImageFiles(Post post){

        List<File> fileList = fileRepository.findAllByPost(post);

        fileStorageControlService.deleteFiles(fileList);

        fileRepository.deleteAll(fileList);
    }

    /**
     * 이미지 파일 다건 삭제 (post에 연관된 File 중 id값이 fileIdList에 없는 File 삭제)
     * @param fileIdList 삭제하지 않을 이미지 파일 id 리스트
     * @param post 연관 게시물
     * @return
     */
    @Transactional
    public void deleteImageFiles(List<Long> fileIdList, Post post){

        List<File> existFileList = fileRepository.findAllByPost(post);

        List<File> deleteFileList = existFileList.stream()
                        .filter(file -> !fileIdList.contains(file.getId()))
                                .collect(Collectors.toList());

        fileStorageControlService.deleteFiles(deleteFileList);

        fileRepository.deleteAll(deleteFileList);
    }
}
