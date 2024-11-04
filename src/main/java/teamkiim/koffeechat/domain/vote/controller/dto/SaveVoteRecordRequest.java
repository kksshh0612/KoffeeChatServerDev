package teamkiim.koffeechat.domain.vote.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 투표 Request")
public class SaveVoteRecordRequest {

    @Schema(description = "사용자가 투표한 투표 항목의 암호화된 pk list", example = "[1, 2]")
    private List<String> items;

}
