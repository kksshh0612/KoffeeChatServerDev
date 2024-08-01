package teamkiim.koffeechat.domain.corp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import teamkiim.koffeechat.domain.corp.service.dto.response.CorpDomainResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "현직자 인증 도메인 관리 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorpApiDocument {

    /**
     * 도메인 승인 요청
     */
    @Operation(summary = "현직자 인증 도메인 검증 요청", description = "현직자 인증 시 회사 도메인 검증 요청을 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "도메인 검증 요청 저장 완료"),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 요청한 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RequestCorpDomain {
    }

    /**
     * 회사 도메인 검색
     */
    @Operation(summary = "회사 도메인 검색", description = "현직자 인증 시 회사 이름/도메인을 통해 검색한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색된 회사 도메인을 반환한다.",
                    content = @Content(schema = @Schema(implementation = CorpDomainResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인하지 않은 사용자가 요청한 경우",
                            value = "{\"code\":401, \"message\":\"로그인해주세요.\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FindCorpDomain {
    }
}
