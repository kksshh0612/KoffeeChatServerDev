package teamkiim.koffeechat.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import teamkiim.koffeechat.domain.file.dto.response.ProfileImageInfoResponse;
import teamkiim.koffeechat.domain.member.dto.response.MemberInfoResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "회원 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberApiDocument {

    /**
     * 프로필 사진 등록
     */
    @Operation(summary = "회원 프로필 사진 등록", description = "사용자가 프로필 사진을 등록한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로필 사진의 정보가 반환된다.",
                    content = @Content(schema = @Schema(implementation = ProfileImageInfoResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "프로필 사진 파일이 잘못된 경우",
                            value = "{\"code\":400, \"message\":\"사진 파일 요청이 올바르지 않습니다.\"}")}
            )),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 프로필 사진을 등록하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "파일 저장에 실패한 경우",
                            value = "{\"code\":500, \"message\":\"파일 저장에 실패했습니다.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SaveProfileImage {
    }

    /**
     * 관심 기술 카테고리 등록
     */
    @Operation(summary = "회원의 관심 기술 카테고리 저장", description = "사용자가 자신의 관심 기술 카테고리를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 관심 기술 설정 완료.",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 관심 기술 카테고리를 설정하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface EnrollSkills {
    }

    /**
     * 본인 프로필 조회
     */
    @Operation(summary = "본인 프로필 조회", description = "사용자가 본인의 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청한 회원 프로필 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 프로필 조회를 시도하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FindProfile {
    }

    /**
     * 회원 프로필 조회
     */
    @Operation(summary = "회원 프로필 조회", description = "사용자가 회원의 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청한 회원 프로필 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 프로필 조회를 시도하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FindMemberProfile {
    }

    /**
     * 프로필 수정
     */
    @Operation(summary = "회원 정보 수정", description = "사용자가 본인의 프로필 정보를 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 정보 수정 완료"),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ModifyProfile {
    }

    /**
     * 사용자 이메일 변경 시 인증 메시지 전송
     */
    @Operation(summary = "사용자 이메일 변경 시 인증 메시지 전송", description = "사용자가 새로운 이메일 인증 메시지를 받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "새로운 이메일로 인증 메시지를 받는다."),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 이메일을 변경하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            )),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "요청한 이메일과 동일한 이메일이 이미 존재하는 경우",
                            value = "{\"code\": 409, \"message\": \"이미 사용중인 이메일입니다.\"}")}
            )),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이메일이 정상적으로 전송되지 않은 경우",
                            value = "{\"code\": 500, \"message\": \"이메일 전송에 실패했습니다. 이메일이 올바른지 확인해주세요.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SendNewAuthEmail {
    }

    /**
     * 이메일 변경
     */
    @Operation(summary = "인증된 이메일로 사용자 이메일을 변경", description = "새로운 인증된 이메일로 사용자 이메일을 변경한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 변경 완료"),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 이메일을 변경하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateNewEmailApiDoc {
    }

    /**
     * 현재 비밀번호 인증
     */
    @Operation(summary = "현재 비밀번호 인증", description = "사용자가 비밀번호 변경을 위해 현재 비밀번호로 본인 인증한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 확인 완료"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "비밀번호 인증 실패",
                            value = "{\"code\":400, \"message\":\"비밀번호가 틀렸습니다.\"}")}
            )),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 이메일을 변경하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CheckCurrentPasswordApiDoc {
    }

    /**
     * 새 비밀번호로 변경
     */
    @Operation(summary = "새 비밀번호로 변경", description = "사용자가 새로운 비밀번호로 비밀번호를 변경한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 완료, 다시 로그인해주세요."),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "비밀번호 요청이 올바르지 않습니다.",
                            value = "{\"code\":400, \"message\":\"기존 비밀번호와 동일한 비밀번호 입니다.\"}"),
                            @ExampleObject(name = "비밀번호 요청이 올바르지 않습니다.",
                                    value = "{\"code\":400, \"message\":\"비밀번호를 확인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 이메일을 변경하려고 하는 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자를 찾을 수 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 회원이 존재하지 않습니다\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdatePasswordApiDoc {
    }
}
