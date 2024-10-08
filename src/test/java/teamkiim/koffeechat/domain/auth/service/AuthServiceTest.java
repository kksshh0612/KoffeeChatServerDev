package teamkiim.koffeechat.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import teamkiim.koffeechat.TestSupport;
import teamkiim.koffeechat.domain.auth.dto.TokenDto;
import teamkiim.koffeechat.domain.auth.dto.request.LoginServiceRequest;
import teamkiim.koffeechat.domain.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.domain.MemberRole;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.SkillCategory;
import teamkiim.koffeechat.global.authentication.Authenticator;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class AuthServiceTest extends TestSupport {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private Authenticator authenticator;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 회원가입 정보를 이용하여 회원가입을 하면 사용자 정보가 저장된다.")
    @Test
    void signUp() {
        // given
        SignUpServiceRequest signUpServiceRequest = SignUpServiceRequest.builder()
                .email("test@test.com")
                .password("test")
                .nickname("nickname")
                .memberRole(MemberRole.FREELANCER)
                .build();

        // when
        authService.signUp(signUpServiceRequest);

        // then
        Optional<Member> member = memberRepository.findByEmail("test@test.com");

        Assertions.assertThat(member).isPresent();
        Assertions.assertThat(member.get().getPassword()).isNotEqualTo("test");
        Assertions.assertThat(passwordEncoder.matches("test", member.get().getPassword())).isEqualTo(true);
        Assertions.assertThat(member.get().getMemberRole()).isEqualTo(MemberRole.FREELANCER);
    }

    @DisplayName("사용자가 이미 존재하는 이메일로 회원가입을 시도하면 에러가 발생한다.")
    @Test
    void signUpWithExistingEmail() {
        // given
        String email = "test@test.com";

        memberRepository.save(createMember(email, "test"));

        SignUpServiceRequest signUpServiceRequest = SignUpServiceRequest.builder()
                .email(email)
                .password("test")
                .nickname("nickname")
                .memberRole(MemberRole.FREELANCER)
                .build();

        // when & then
        Assertions.assertThatThrownBy(() -> authService.signUp(signUpServiceRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXIST);
    }

    @DisplayName("이메일, 비빌번호를 이용하여 로그인한다.")
    @Test
    void login() {
        // given
        String email = "test@test.com";

        Member saveMember = memberRepository.save(createMember(email, passwordEncoder.encode("test")));

        LoginServiceRequest loginServiceRequest = LoginServiceRequest.builder()
                .email(email)
                .password("test")
                .build();

        BDDMockito.given(authenticator.authenticate(any(Member.class)))
                .willReturn(new TokenDto("accessToken", "refreshToken"));

        // when
        TokenDto jwtTokenDto = authService.login(loginServiceRequest);

        // then
        Assertions.assertThat(jwtTokenDto.getAccessToken()).isEqualTo("accessToken");
        Assertions.assertThat(jwtTokenDto.getRefreshToken()).isEqualTo("refreshToken");
    }

    @DisplayName("존재하지 않는 이메일로 로그인을 시도하면 예외가 발생한다.")
    @Test
    void loginWithNotExistingEmail() {
        // given
        String email = "test@test.com";

        Member saveMember = memberRepository.save(createMember(email, passwordEncoder.encode("test")));

        LoginServiceRequest loginServiceRequest = LoginServiceRequest.builder()
                .email("NoExistEmail")
                .password("test")
                .build();

        BDDMockito.given(authenticator.authenticate(any(Member.class)))
                .willReturn(new TokenDto("accessToken", "refreshToken"));

        // when & then
        Assertions.assertThatThrownBy(() -> authService.login(loginServiceRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @DisplayName("일치하지 않는 비밀번호로 로그인을 시도하면 예외가 발생한다.")
    @Test
    void loginWithNotMatchPassword() {
        // given
        String email = "test@test.com";

        Member saveMember = memberRepository.save(createMember(email, passwordEncoder.encode("test")));

        LoginServiceRequest loginServiceRequest = LoginServiceRequest.builder()
                .email("test@test.com")
                .password("NotMatchPassword")
                .build();

        BDDMockito.given(authenticator.authenticate(any(Member.class)))
                .willReturn(new TokenDto("accessToken", "refreshToken"));

        // when & then
        Assertions.assertThatThrownBy(() -> authService.login(loginServiceRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_PASSWORD_NOT_MATCH);
    }

    private Member createMember(String email, String password){
        return Member.builder()
                .email(email)
                .password(password)
                .nickname("test")
                .memberRole(MemberRole.FREELANCER)
                .interestSkillCategoryList(null)
                .profileImageUrl(null)
                .build();
    }

}