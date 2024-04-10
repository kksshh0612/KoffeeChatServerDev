package teamkiim.koffeechat.global.passwordEncrypt;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordEncryptor {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String getSalt(){

        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    public String sha512WithSaltEncode(String plainText, String salt){

        return Hashing.sha512()
                .hashString(plainText + salt, StandardCharsets.UTF_8)
                .toString();
    }
}
