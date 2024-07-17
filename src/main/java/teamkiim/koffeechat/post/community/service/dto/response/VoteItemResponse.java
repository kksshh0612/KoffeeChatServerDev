package teamkiim.koffeechat.post.community.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.vote.domain.VoteItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteItemResponse {

    private String voteItem;                    //투표 항목
    private Long voteCount;                     //투표 횟수

    public static VoteItemResponse of(VoteItem voteItem) {
        return VoteItemResponse.builder()
                .voteItem(voteItem.getItemText())
                .voteCount(voteItem.getVoteCount())
                .build();
    }
}
