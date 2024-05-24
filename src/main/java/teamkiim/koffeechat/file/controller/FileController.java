package teamkiim.koffeechat.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.file.service.FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "파일 API")
public class FileController {

    private final FileService fileService;

    /**
     * 이미지 단건 저장
     */
    @PostMapping("/image")
    @Operation(summary = "이미지 파일 단건 저장", description = "이미지 파일을 단건 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            )),
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

    /**
     * 이미지 삭제
     */
    @DeleteMapping("/image")
    @Operation(summary = "이미지 파일 삭제", description = "이미지 파일을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "postId에 해당하는 게시글이 없는 경우",
                            value = "{\"code\":404, \"message\":\"해당 게시글이 존재하지 않습니다.\"}")}
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "fileName에 해당하는 파일이 없는 경우",
                            value = "{\"code\":404, \"message\":\"존재하지 않는 파일입니다.\"}")}
            ))
    })
    public ResponseEntity<?> deleteImageFile(@RequestParam("fileName") String fileName){

        return fileService.deleteImageFile(fileName);
    }
}
