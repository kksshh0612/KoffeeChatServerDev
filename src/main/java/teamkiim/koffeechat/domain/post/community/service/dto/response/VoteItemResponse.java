package teamkiim.koffeechat.domain.post.community.service.dto.response;

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

    private Long voteItemId;                    //투표 항목 pk
    private String voteItem;                    //투표 항목
    private Long voteCount;                     //투표 횟수

    public static VoteItemResponse of(VoteItem voteItem, boolean isMemberVoted) {
        if (isMemberVoted) {  // 로그인한 사용자가 투표한 경우 : 투표 결과 보여줌
            return VoteItemResponse.builder()
                    .voteItemId(voteItem.getId())
                    .voteItem(voteItem.getItemText())
                    .voteCount(voteItem.getVoteCount())
                    .build();
        } else {              // 투표 안 한 경우 : 투표 결과 숨김
            return VoteItemResponse.builder()
                    .voteItemId(voteItem.getId())
                    .voteItem(voteItem.getItemText())
                    .voteCount(0L)
                    .build();
        }
    }
}
