package teamkiim.koffeechat.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.service.FileService;
import teamkiim.koffeechat.domain.file.dto.response.ImagePathResponse;
import teamkiim.koffeechat.global.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "파일 API")
public class FileController {

    private final FileService fileService;

    /**
     * 이미지 단건 저장
     */
    @Auth(role = {Auth.MemberRole.COMPANY_EMPLOYEE, Auth.MemberRole.FREELANCER, Auth.MemberRole.STUDENT,
            Auth.MemberRole.COMPANY_EMPLOYEE_TEMP, Auth.MemberRole.MANAGER, Auth.MemberRole.ADMIN})
    @PostMapping("/image")
    @Operation(summary = "이미지 파일 단건 저장", description = "이미지 파일을 단건 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성된 이미지 파일의 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = ImagePathResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            )),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "FILE I/O 에러가 난 경우",
                            value = "{\"code\":500, \"message\":\"파일 저장에 실패했습니다.\"}")}
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "파일 디렉토리가 존재하지 않거나, 파일 형식이 잘못된 경우",
                            value = "{\"code\":400, \"message\":\"파일 요청이 올바르지 않습니다.\"}")}
            ))
    })
    public ResponseEntity<?> saveImageFile(@RequestPart(value = "file") MultipartFile multipartFile,
                                           @RequestPart(value = "postId") Long postId){

        return fileService.saveImageFile(multipartFile, postId);
    }

}
