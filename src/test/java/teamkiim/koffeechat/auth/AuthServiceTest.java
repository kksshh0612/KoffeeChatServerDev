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
import teamkiim.koffeechat.auth.dto.request.SignUpRequest;
import teamkiim.koffeechat.auth.service.AuthService;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;
import teamkiim.koffeechat.member.service.MemberService;

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
        final SignUpRequest signUpRequest = new SignUpRequest("test@naver.com", "testPw", "testNickname");
        BDDMockito.given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

        //when
        ResponseEntity<?> response = authService.signUp(signUpRequest);

        //then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
