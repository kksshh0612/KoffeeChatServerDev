package teamkiim.koffeechat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD REQUEST
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PASSWORD_EQUAL(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호입니다."),
    EMAIL_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "이메일, 비밀번호가 일치하지 않습니다"),
    AUTH_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),

    POST_REQUEST_WITHOUT_TITLE(HttpStatus.BAD_REQUEST, "제목을 입력해주세요"),
    POST_REQUEST_WITHOUT_CONTENT(HttpStatus.BAD_REQUEST, "내용을 입력해주세요"),

    INVALID_VOTE_REQUEST(HttpStatus.BAD_REQUEST, "투표 요청이 올바르지 않습니다."),

    FILE_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "파일 요청이 올바르지 않습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다. 다시 로그인 하세요"),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원에 권한이 없습니다."),
    VOTE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 회원에 투표 생성 권한이 없습니다."),
    CORP_DOMAIN_FORBIDDEN(HttpStatus.FORBIDDEN, "승인 거절된 도메인입니다. 이메일 확인 후 문의해주세요."),
    CHAT_ROOM_ALREADY_FULL(HttpStatus.FORBIDDEN, "이미 가득 찬 채팅방입니다. 관리자에게 문의하세요."),

    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"),
    MEMBER_CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방에 해당하는 회원이 존재하지 않습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요가 존재하지 않습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크가 존재하지 않습니다."),
    MEMBER_FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER FOLLOW가 존재하지 않습니다."),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "투표가 존재하지 않습니다."),
    VOTE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "투표 항목이 존재하지 않습니다."),
    CORP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회사 도메인이 존재하지 않습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림이 존재하지 않습니다."),

    // 409 CONFLICT
    EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 회원가입된 이메일입니다."),
    CHAT_ROOM_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 채팅방이 존재합니다."),
    POST_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 작성된 게시글입니다."),
    VOTE_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 투표되었습니다."),
    CORP_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 회사 도메인입니다."),
    ALREADY_JOINED_CHAT_ROOM(HttpStatus.CONFLICT, "이미 참여중인 채팅방입니다"),
    CORP_REQUEST_ALREADY_EXIST(HttpStatus.CONFLICT, "요청이 처리중입니다. 요청 처리 결과는 알림으로 발송됩니다."),
    CORP_DOMAIN_WAITING(HttpStatus.CONFLICT, "등록 요청이 되어있는 도메인입니다. 요청 처리 결과는 알림으로 발송됩니다."),

    // 500 INTERNAL_SERVER_ERROR
    JSON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 사용자 정보 json 파싱 에러"),
    CANNOT_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다. 이메일이 올바른지 확인해주세요"),
    FILE_IO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 알림 종류"),
    ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "암호화 오류"),
    DECRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "복호화 오류"),

    // 503 SERVICE_UNAVAILABLE
    EXTERNAL_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "외부 인증 서버로부터 응답이 없습니다. 잠시 후에 재시도해주세요.");

    private final HttpStatus httpStatus;
    private final String msg;
}
