package teamkiim.koffeechat.post.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.vote.domain.VoteItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteItemInfoDto {

    private Long id;
    private String itemText;
    private Long voteCount;

    public static VoteItemInfoDto of(VoteItem voteItem) {
        return VoteItemInfoDto.builder()
                .id(voteItem.getId())
                .itemText(voteItem.getItemText())
                .voteCount(voteItem.getVoteCount())
                .build();
    }
}
