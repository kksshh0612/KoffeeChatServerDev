package teamkiim.koffeechat.domain.vote.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.vote.domain.VoteItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveVoteRecordServiceDto {

    private Long id;                                        // 투표 항목 id
    private String itemText;
    private Long voteCount;

    public static SaveVoteRecordServiceDto of(VoteItem voteItem) {
        return SaveVoteRecordServiceDto.builder()
                .id(voteItem.getId())
                .itemText(voteItem.getItemText())
                .voteCount(voteItem.getVoteCount())
                .build();
    }
}
