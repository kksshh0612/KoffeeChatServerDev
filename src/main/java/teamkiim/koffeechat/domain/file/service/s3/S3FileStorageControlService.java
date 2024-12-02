package teamkiim.koffeechat.domain.file.service.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

/**
 * S3 물리적 파일 I/O 담당
 */
@Component
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class S3FileStorageControlService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * Presigned Url 생성
     *
     * @param fileName 파일 이름
     * @return presignedUrl
     */
    public String createPresignedUrl(String fileName) {

        Date expiration = getExpiration();          // 만료 시각

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toExternalForm();
    }

    /**
     * 파일 단건 삭제
     *
     * @param url
     */
    public void deleteFile(String url) {

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, parseObjectName(url));

        try {
            amazonS3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        }
    }

    /**
     * 파일 다건 삭제
     *
     * @param urls 삭제할 파일 url 리스트
     */
    public void deleteFiles(List<String> urls) {

        List<DeleteObjectsRequest.KeyVersion> keysToDelete = urls.stream()
                .map(url -> new DeleteObjectsRequest.KeyVersion(parseObjectName(url)))
                .toList();

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keysToDelete);

        try {
            amazonS3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        }

    }

    private String parseObjectName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private static Date getExpiration() {

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60;     // 1분
        expiration.setTime(expTimeMillis);

        return expiration;
    }
}
