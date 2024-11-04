package teamkiim.koffeechat.domain.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.vote.domain.VoteItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteItemResponse {

    private String voteItemId;                    //투표 항목 pk
    private String voteItemText;                    //투표 항목
    private long voteCount;                     //투표 횟수

    public static VoteItemResponse of(String voteItemId, VoteItem voteItem, boolean isMemberVoted) {
        return VoteItemResponse.builder()
                .voteItemId(voteItemId)
                .voteItemText(voteItem.getItemText())
                .voteCount(isMemberVoted ? voteItem.getVoteCount() : 0)
                .build();
    }
}
