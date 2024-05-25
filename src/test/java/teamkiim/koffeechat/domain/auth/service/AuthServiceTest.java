package teamkiim.koffeechat.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import teamkiim.koffeechat.domain.auth.dto.TokenDto;
import teamkiim.koffeechat.domain.auth.dto.request.LoginServiceRequest;
import teamkiim.koffeechat.domain.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.authentication.Authenticator;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private Authenticator authenticator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원가입에 성공한다.")
    @Test
    void signUp_shouldSaveMember_whenEmailDoesNotExist() {
        // given
        String email = "test@example.com";
        String password = "password123";
        SignUpServiceRequest request = new SignUpServiceRequest(email, password, "test", MemberRole.FREELANCER);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        authService.signUp(request);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("이메일 중복 시 예외가 발생한다.")
    @Test
    void signUp_shouldThrowException_whenEmailAlreadyExists() {
        // given
        String email = "test@example.com";
        SignUpServiceRequest request = new SignUpServiceRequest(email, "password123");
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(new Member()));

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EMAIL_ALREADY_EXIST.getMsg());
    }

    @DisplayName("로그인에 성공한다.")
    @Test
    void login_shouldReturnToken_whenEmailAndPasswordAreValid() {
        // given
        String email = "test@example.com";
        String password = "password123";
        LoginServiceRequest request = new LoginServiceRequest(email, password);
        Member member = new Member();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(true);
        when(authenticator.authenticate(member)).thenReturn(new TokenDto("accessToken", "refreshToken"));

        // when
        TokenDto token = authService.login(request);

        // then
        verify(authenticator, times(1)).authenticate(member);
    }

    @DisplayName("존재하지 않는 이메일로 로그인을 시도하면 예외가 발생한다.")
    @Test
    void login_shouldThrowException_whenEmailDoesNotExist() {
        // given
        String email = "test@example.com";
        LoginServiceRequest request = new LoginServiceRequest(email, "password123");
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMsg());
    }

    @DisplayName("비밀번호가 불일치하면 예외가 발생한다.")
    @Test
    void login_shouldThrowException_whenPasswordDoesNotMatch() {
        // given
        String email = "test@example.com";
        String password = "password123";
        LoginServiceRequest request = new LoginServiceRequest(email, password);
        Member member = new Member();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EMAIL_PASSWORD_NOT_MATCH.getMsg());
    }
}
