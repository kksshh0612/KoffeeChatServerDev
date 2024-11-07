package teamkiim.koffeechat.domain.chat.room.direct.controller;

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
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.chat.room.direct.dto.response.CreateDirectChatRoomResponse;

@Tag(name = "DIRECT(일대일) 채팅방 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectChatRoomApiDocument {

    /**
     * 일대일 채팅방 생성
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 채팅방 id를 반환한다.",
                    content = @Content(schema = @Schema(implementation = CreateDirectChatRoomResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원을 찾을 수 없는 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface create {
    }

    /**
     * 회원이 현재 속해있는 일대일 채팅방 목록 페이징 조회
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원이 현재 속한 채팅방 정보 리스트를 반환한다.",
                    content = @Content(schema = @Schema(implementation = ChatRoomListResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원-채팅방을 찾을 수 없는 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface findChatRooms {
    }

    /**
     * 채팅방 퇴장
     */
    @Operation(summary = "채팅방 퇴장", description = "사용자가 현재 참여중인 채팅방을 퇴장한다.")
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
    @interface exit {
    }
}
