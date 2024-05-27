package teamkiim.koffeechat.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import teamkiim.koffeechat.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.auth.service.AuthService;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    void signUp(){
        //given
        final Member member = new Member();
        final SignUpServiceRequest signUpServiceRequest = new SignUpServiceRequest("test@naver.com", "testPw", "testNickname");
        BDDMockito.given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

        //when
        ResponseEntity<?> response = authService.signUp(signUpServiceRequest);

        //then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
