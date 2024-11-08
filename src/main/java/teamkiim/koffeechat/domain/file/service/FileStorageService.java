package teamkiim.koffeechat.domain.file.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;

public interface FileStorageService {

    ImageUrlResponse uploadFile(String saveFileUrl, MultipartFile multipartFile);

    void deleteFile(String url);

    void deleteFiles(List<String> urls);
}
