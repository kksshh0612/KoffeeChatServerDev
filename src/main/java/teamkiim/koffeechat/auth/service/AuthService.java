package teamkiim.koffeechat.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.auth.dto.request.LoginRequest;
import teamkiim.koffeechat.auth.dto.request.SignUpRequest;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.passwordEncrypt.PasswordEncryptor;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.domain.MemberRole;
import teamkiim.koffeechat.member.domain.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncryptor passwordEncryptor;

    private static final String accessTokenName = "Authorization";
    private static final String refreshTokenName = "refresh-token";

    /**
     * 회원가입
     */
    @Transactional
    public ResponseEntity<?> signUp(SignUpRequest signUpRequest){

        Optional<Member> existMember = memberRepository.findByEmail(signUpRequest.getEmail());

        if(existMember.isPresent()) throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);

        Member member = Member.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .nickname(signUpRequest.getNickname())
                .role(MemberRole.USER)
                .imageUrl(null)
                .socialLoginId(null)
                .build();

//        member.encodePassword(passwordEncryptor);

        memberRepository.save(member);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인
     */
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response){

        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(member.getPassword().equals(loginRequest.getPassword())){
            cookieProvider.setCookie(accessTokenName,
                    jwtTokenProvider.createAccessToken(member.getRole().toString(), member.getId()),
                    false, response);
            cookieProvider.setCookie(refreshTokenName,
                    jwtTokenProvider.createRefreshToken(member.getRole().toString(), member.getId()),
                    false, response);
        }

        return ResponseEntity.ok("로그인이 완료되었습니다.");
    }
}
