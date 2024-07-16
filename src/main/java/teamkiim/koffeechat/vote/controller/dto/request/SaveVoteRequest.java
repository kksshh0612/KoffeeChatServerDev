package teamkiim.koffeechat.vote.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.common.domain.Post;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.domain.VoteItem;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveVoteRequest {

    private Long id;

    @NotEmpty(message="항목을 입력해주세요.")
    private List<String> items;

    public Vote toEntity(Post post) {
        Vote vote = new Vote(post);
        for (String item : items) {
            VoteItem voteItem = new VoteItem(vote, item);
            vote.addVoteItem(voteItem);
        }
        return vote;
    }
}


