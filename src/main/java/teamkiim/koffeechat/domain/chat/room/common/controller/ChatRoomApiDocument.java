package teamkiim.koffeechat.domain.chat.room.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import teamkiim.koffeechat.domain.chat.message.dto.response.ChatMessageResponse;

@Tag(name = "채팅방 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatRoomApiDocument {

    /**
     * 채팅 메세지 조회 (커서 기반 페이징)
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 정보 리스트가 반환된다.",
                    content = @Content(schema = @Schema(implementation = ChatMessageResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "채팅방을 찾을 수 없는 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface open {
    }

    /**
     * 채팅방 닫기
     */
    @Operation(summary = "채팅방 닫기", description = "사용자가 채팅방을 닫는다. (퇴장 아님)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok"),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "채팅방을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원-채팅방을 찾을 수 없는 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface close {
    }

}
