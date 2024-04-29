package teamkiim.koffeechat.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.file.repository.FileRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.post.domain.Post;
import teamkiim.koffeechat.post.repository.PostRepository;

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
     * @param postId 게시물 PK
     * @return ImagePathResponse
     */
    @Transactional
    public ResponseEntity<?> saveImageFile(MultipartFile multipartFile, Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        File file = new File(post);

        fileStorageControlService.saveFile(file, multipartFile);

        return ResponseEntity.ok(new ImagePathResponse(file.getPath(), file.getName()));
    }

    /**
     * 이미지 파일 삭제
     * @param fileName 저장 파일명
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> deleteImageFile(String fileName){

        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        fileStorageControlService.deleteFile(file);

        fileRepository.delete(file);

        return ResponseEntity.ok("이미지 파일 삭제 완료");
    }
}
