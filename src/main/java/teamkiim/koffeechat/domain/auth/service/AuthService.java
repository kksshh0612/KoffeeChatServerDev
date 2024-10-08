package teamkiim.koffeechat.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamkiim.koffeechat.domain.auth.dto.TokenDto;
import teamkiim.koffeechat.domain.auth.dto.request.LoginServiceRequest;
import teamkiim.koffeechat.domain.auth.dto.request.SignUpServiceRequest;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.member.repository.MemberRepository;
import teamkiim.koffeechat.global.authentication.Authenticator;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final Authenticator authenticator;
    private final PasswordEncoder passwordEncoder;

    @Value("${file-path}")
    private String baseFilePath;

    @Value("${basic-profile-image-url}")
    private String basicProfileImageUrl;

    /**
     * 회원가입
     *
     * @param signUpServiceRequest 회원가입 요청 dto
     * @return HttpStatus.CREATED
     */
    @Transactional
    public void signUp(SignUpServiceRequest signUpServiceRequest) {

        Optional<Member> existMember = memberRepository.findByEmail(signUpServiceRequest.getEmail());

        if (existMember.isPresent()) throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);

        Member member = signUpServiceRequest.toEntity(Paths.get(baseFilePath, basicProfileImageUrl).toString());

        member.encodePassword(passwordEncoder.encode(member.getPassword()));

        memberRepository.save(member);
    }

    /**
     * 로그인
     *
     * @param loginServiceRequest 로그인 요청 dto
     * @return ok
     */
    public TokenDto login(LoginServiceRequest loginServiceRequest) {

        Member member = memberRepository.findByEmail(loginServiceRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginServiceRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.EMAIL_PASSWORD_NOT_MATCH);
        }

        return authenticator.authenticate(member);
    }

    /**
     * 로그아웃
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ok
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        authenticator.invalidate(request, response);
    }

}
