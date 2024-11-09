package teamkiim.koffeechat.domain.chat.room.tech.controller;

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
import teamkiim.koffeechat.domain.chat.room.tech.dto.response.EnterTechChatRoomResponse;
import teamkiim.koffeechat.domain.chat.room.tech.dto.response.TechChatRoomListResponse;

@Tag(name = "Tech(기술) 채팅방 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TechChatRoomApiDocument {

    /**
     * 채팅방 최초 생성
     */
    @Operation(summary = "현재 참여중인 채팅방 목록 페이징 조회", description = "사용자가 현재 참여중인 채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok"),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이미 해당 기술 채팅방이 존재하는 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface create {
    }

    /**
     * 기술 채팅방 목록 페이징 조회
     */
    @Operation(summary = "채팅방 목록 페이징 조회", description = "채팅방의 목록을 페이징해 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 정보 리스트를 반환한다.",
                    content = @Content(schema = @Schema(implementation = TechChatRoomListResponse.class)))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface findChatRooms {
    }

    /**
     * 채팅방 참여
     */
    @Operation(summary = "채팅방 참여", description = "사용자가 기술 채팅방에 참여한다. (이후, 채팅방 입장하는 API 호출하기)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = EnterTechChatRoomResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이미 채팅방이 가득 찬 경우")})),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "회원을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "채팅방을 찾을 수 없는 경우")})),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이미 해당 채팅방에 참여중인 경우")}))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface enter {
    }

    /**
     * 회원이 현재 속해있는 기술 채팅방 목록 페이징 조회
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
    @interface findJoinChatRooms {
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
