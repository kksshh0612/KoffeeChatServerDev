package teamkiim.koffeechat.domain.vote.dto;

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

    private String id;                                        // 투표 항목 id
    private String itemText;
    private Long voteCount;

    public static SaveVoteRecordServiceDto of(String voteItemId, VoteItem voteItem) {
        return SaveVoteRecordServiceDto.builder()
                .id(voteItemId)
                .itemText(voteItem.getItemText())
                .voteCount(voteItem.getVoteCount())
                .build();
    }
}
