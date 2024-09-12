package teamkiim.koffeechat.domain.notification.controller.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import teamkiim.koffeechat.domain.notification.dto.response.NotificationListItemResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "알림 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationApiDocument {

    /**
     * SSE 연결 설정
     */
    @Operation(summary = "서버와 클라이언트 간 SSE 연결 설정", description = "클라이언트에서 서버로 보낸 연결 요청에 대한 응답으로 연결 설정을 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 연결 요청을 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "memberId에 해당하는 사용자가 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SubscribeApiDoc {
    }

    /**
     * 페이지 로딩 시 읽지 않은 알림 개수 조회
     */
    @Operation(summary = "페이지 로딩 시 읽지 않은 알림 개수 조회", description = "읽지 않은 알림 개수를 DB에서 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽지 않은 알림 개수 반환",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 연결 요청을 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "memberId에 해당하는 사용자가 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface getUnreadNotificationCountApiDoc {
    }

    /**
     * 알림 목록 조회
     */
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회해온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 리스트를 반환한다.",
                    content = @Content(schema = @Schema(implementation = NotificationListItemResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 알림 목록을 조회하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "memberId에 해당하는 회원이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ShowListApiDoc {
    }

    /**
     * 알림 확인
     */
    @Operation(summary = "특정 알림 확인", description = "사용자가 notificationId에 해당하는 알림을 확인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 특정 알림을 확인한 후의 읽지 않은 알림 개수를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 알림을 확인하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "memberId에 해당하는 회원이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}"),
                            @ExampleObject(name = "notificationId에 해당하는 알림이 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 알림이 존재하지 않습니다.\"}")}

            ))

    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateIsReadApiDoc {
    }

    /**
     * 알림 단건 삭제
     */
    @Operation(summary = "알림 단건 삭제", description = "사용자가 notificationId에 해당하는 알림을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 특정 알림을 삭제한 후의 읽지 않은 알림 개수를 반환한다.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 알림을 삭제하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "memberId에 해당하는 회원이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}"),
                            @ExampleObject(name = "notificationId에 해당하는 알림이 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 알림이 존재하지 않습니다.\"}")}

            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeleteApiDoc {
    }

}
