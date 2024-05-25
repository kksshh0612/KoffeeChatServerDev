package teamkiim.koffeechat.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.auth.dto.request.LoginServiceRequest;
import teamkiim.koffeechat.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.redis.util.RedisUtil;
import teamkiim.koffeechat.member.domain.Member;
import teamkiim.koffeechat.member.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    private static final String accessTokenName = "Authorization";
    private static final String refreshTokenName = "refresh-token";

    @Value("${jwt.refresh.exp}")
    private long refreshTokenExpTime;

    /**
     * 회원가입
     * @param signUpServiceRequest 회원가입 요청 dto
     * @return ok
     */
    @Transactional
    public ResponseEntity<?> signUp(SignUpServiceRequest signUpServiceRequest){

        Optional<Member> existMember = memberRepository.findByEmail(signUpServiceRequest.getEmail());

        if(existMember.isPresent()) throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);

        Member member = signUpServiceRequest.toEntity();

        member.encodePassword(passwordEncoder);

        memberRepository.save(member);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인
     * @param loginServiceRequest 로그인 요청 dto
     * @param response HttpServletResponse
     * @return ok
     */
    public ResponseEntity<?> login(LoginServiceRequest loginServiceRequest, HttpServletResponse response){

        Member member = memberRepository.findByEmail(loginServiceRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(!member.matchPassword(passwordEncoder, loginServiceRequest.getPassword())){
            throw new CustomException(ErrorCode.EMAIL_PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberRole().toString(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberRole().toString(), member.getId());

        // 레디스 세팅
        redisUtil.setData(refreshToken, "refresh-token", refreshTokenExpTime);

        // 쿠키 세팅
        cookieProvider.setCookie(accessTokenName, accessToken, false, response);
        cookieProvider.setCookie(refreshTokenName, refreshToken, false, response);

        return ResponseEntity.ok("로그인이 완료되었습니다.");
    }

//    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
//
//        String accessToken = jwtTokenProvider.getTokenClaims()
//        jwtTokenProvider.invalidateAccessToken();
//    }
}
