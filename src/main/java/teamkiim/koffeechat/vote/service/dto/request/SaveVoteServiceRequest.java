package teamkiim.koffeechat.vote.service.dto.request;

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
public class SaveVoteServiceRequest {

    private String title;                   //투표 제목
    private List<String> items;             //투표 항목

    public Vote toEntity(Post post, String title) {
        Vote vote = new Vote(post, title);
        for (String item : items) {
            VoteItem voteItem = new VoteItem(vote, item);
            vote.addVoteItem(voteItem);
        }
        return vote;
    }
}
