package teamkiim.koffeechat.global.aescipher;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

@Component
public class AESCipher {

    public static final String algorithm = "AES";  // 대칭키 암호화 알고리즘

    @Value("${spring.aes.secret}")
    private String secretKey;

    // 대칭키 생성
    public SecretKey generateKey() throws Exception {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, algorithm);
    }

    // 암호화
    public String encrypt(final Long value) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey());  //암호화 모드로 비밀키 전달
        byte[] valueBytes = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
        byte[] encrypted = cipher.doFinal(valueBytes);    //암호화한 메시지를 포함한 byte 배열 반환
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    // 복호화
    public Long decrypt(final String encryptedValue) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, generateKey());  //복호화 모드로 암호화때 사용한 동일한 비밀키 전달
        byte[] encryptedBytes = Base64.getUrlDecoder().decode(encryptedValue);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return ByteBuffer.wrap(decryptedBytes).getLong();
    }
}
