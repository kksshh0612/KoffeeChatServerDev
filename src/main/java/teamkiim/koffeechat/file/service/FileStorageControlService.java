package teamkiim.koffeechat.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.file.domain.File;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 물리적 파일 I/O 담당
 */
@Component
public class FileStorageControlService {

    @Value("${file-path}")
    private static String filePath;

    /**
     * 파일 단건 저장
     * @param file 파일 도메인
     * @param multipartFile 실제 파일
     */
    public void saveFile(File file, MultipartFile multipartFile){

        StringBuilder filePathBuilder = new StringBuilder(filePath)
                .append(file.getPath())
                .append(java.io.File.separator)
                .append(file.getName());

        Path savePath = Paths.get(filePathBuilder.toString());

        try {
            multipartFile.transferTo(savePath);
        } catch (IOException e){
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        } catch (IllegalStateException e){
            throw new CustomException(ErrorCode.FILE_REQUEST_ERROR);
        }
    }

    /**
     * 파일 단건 삭제
     * @param file 파일 도메인
     */
    public void deleteFile(File file){

        StringBuilder filePathBuilder = new StringBuilder(filePath)
                .append(file.getPath())
                .append(java.io.File.separator)
                .append(file.getName());

        Path savePath = Paths.get(filePathBuilder.toString());

        java.io.File deleteFile = new java.io.File(savePath.toString());

        if(!deleteFile.exists()){
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        }

        deleteFile.delete();
    }
}
