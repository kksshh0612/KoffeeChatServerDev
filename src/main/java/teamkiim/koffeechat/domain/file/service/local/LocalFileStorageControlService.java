package teamkiim.koffeechat.domain.file.service.local;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.service.FileStorageService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 로컬 환경 물리적 파일 I/O 담당
 */
@Component
@Profile("local")
public class LocalFileStorageControlService implements FileStorageService {

    /**
     * 파일 단건 업로드
     *
     * @param saveFileUrl   파일 업로드 경로
     * @param multipartFile 업로드 파일
     */
    public ImageUrlResponse uploadFile(String saveFileUrl, MultipartFile multipartFile) {

        Path savePath = Paths.get(saveFileUrl);

        try {
            multipartFile.transferTo(savePath);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        } catch (IllegalStateException e) {
            throw new CustomException(ErrorCode.FILE_REQUEST_ERROR);
        }

        return ImageUrlResponse.of(saveFileUrl);
    }

    /**
     * 파일 단건 삭제
     *
     * @param url 삭제할 파일 경로
     */
    public void deleteFile(String url) {

        Path deletePath = Paths.get(url);

        try {
            // 파일이 존재하는지 확인 후 삭제 시도
            if (Files.exists(deletePath)) {
                Files.delete(deletePath);
            } else {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND); // 파일이 없을 경우 예외 발생
            }
        } catch (NoSuchFileException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND); // 파일을 찾지 못한 경우
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED); // 파일 I/O 에러 처리
        }
    }

    /**
     * 파일 다건 삭제
     *
     * @param fileList 삭제할 파일 엔티티 리스트
     */
    public void deleteFiles(List<File> fileList) {

        for (File file : fileList) {

            Path savePath = Paths.get(file.getUrl());

            try {
                Files.deleteIfExists(savePath);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_IO_FAILED);
            }
        }
    }

}
