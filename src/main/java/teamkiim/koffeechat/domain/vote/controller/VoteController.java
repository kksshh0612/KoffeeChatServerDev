package teamkiim.koffeechat.domain.vote.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.vote.controller.dto.SaveVoteRecordRequest;
import teamkiim.koffeechat.domain.vote.dto.SaveVoteRecordServiceDto;
import teamkiim.koffeechat.domain.vote.service.VoteService;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
@Tag(name = "투표 API")
public class VoteController {

    private final VoteService voteService;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 투표
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("/{postId}/votes")
    @VoteApiDocument.SaveVoteRecord
    public ResponseEntity<?> saveVoteRecord(@PathVariable("postId") String postId,
                                            @Valid @RequestBody SaveVoteRecordRequest saveVoteRecordRequest,
                                            HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(postId);

        List<Long> decryptedVoteItemList = saveVoteRecordRequest.getItems().stream().map(aesCipherUtil::decrypt)
                .toList();

        List<SaveVoteRecordServiceDto> responses = voteService.saveVoteRecord(decryptedPostId, decryptedVoteItemList,
                memberId);

        return ResponseEntity.ok(responses);
    }
}
