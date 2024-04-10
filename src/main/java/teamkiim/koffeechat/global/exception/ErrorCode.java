package teamkiim.koffeechat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD REQUEST
    EMAIL_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "이메일, 비밀번호가 일치하지 않습니다"),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다. 다시 로그인 하세요"),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원에 권한이 없습니다"),

    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다"),

    // 409 CONFLICT
    EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 회원가입된 이메일입니다.");


    private final HttpStatus httpStatus;
    private final String msg;
}