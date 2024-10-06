package teamkiim.koffeechat.domain.file.service;

import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;

import java.util.List;

public interface FileStorageService {

    ImageUrlResponse uploadFile(String saveFileUrl, MultipartFile multipartFile);

    void deleteFile(String url);

    void deleteFiles(List<File> fileList);
}
