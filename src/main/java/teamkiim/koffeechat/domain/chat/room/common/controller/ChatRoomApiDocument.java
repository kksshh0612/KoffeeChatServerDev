package teamkiim.koffeechat.domain.chat.room.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import teamkiim.koffeechat.domain.chat.room.common.dto.response.ChatRoomListResponse;
import teamkiim.koffeechat.domain.file.dto.response.ProfileImageInfoResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "채팅방 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatRoomApiDocument {

    /**
     * 채팅방 목록 페이징 조회
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 정보 리스트가 반환된다.",
                    content = @Content(schema = @Schema(implementation = ChatRoomListResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}")
                    })
            )
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FindChatRoomsByTypeApiDoc {}

    /**
     * 채팅방 id로 채팅방 정보 단건 조회
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 정보 리스트가 반환된다.",
                    content = @Content(schema = @Schema(implementation = ChatRoomListResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우",
                                    value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다.\"}")
                    })
            )
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FindChatRoomByChatRoomIdApiDoc {}
}
