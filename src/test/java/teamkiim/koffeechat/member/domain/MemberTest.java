package teamkiim.koffeechat.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("비밀번호를 암호화한다.")
    @Test
    void encodePassword() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();

        // when
        member.encodePassword(passwordEncoder);

        // then
        assertThat(member.getPassword()).isNotEqualTo("test");
    }

    private Member createMember(){
        return Member.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();
    }

}