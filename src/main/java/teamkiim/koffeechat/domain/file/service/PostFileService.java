package teamkiim.koffeechat.domain.file.service;

import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.post.common.domain.Post;

import java.util.List;

public interface PostFileService {

    ImageUrlResponse uploadImageFile(MultipartFile multipartFile, Long postId);

    void deleteImageFiles(Post post);

    void deleteImageFiles(List<Long> fileIdList, Post post);
}
