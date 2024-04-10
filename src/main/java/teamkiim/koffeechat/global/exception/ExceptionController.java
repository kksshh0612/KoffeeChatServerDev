package teamkiim.koffeechat.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e, HttpServletRequest request){

        log.error(e.getErrorCode().getMsg(), e.getStackTrace()[0]);        // 인덱스 0 -> 실제 예외 발생 지점

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode().getMsg());
    }


}
