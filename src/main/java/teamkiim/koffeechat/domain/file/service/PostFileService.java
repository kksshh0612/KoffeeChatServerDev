package teamkiim.koffeechat.domain.file.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.post.common.domain.Post;

public interface PostFileService {

    ImageUrlResponse uploadImageFile(MultipartFile multipartFile, Long postId);

    void deleteImageFiles(Post post);

    void deleteImageFiles(List<String> fileUrlList, Post post);
}


