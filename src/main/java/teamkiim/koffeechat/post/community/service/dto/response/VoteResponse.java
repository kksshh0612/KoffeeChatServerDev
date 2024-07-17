package teamkiim.koffeechat.post.community.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.vote.domain.Vote;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    private String title;                                   //투표 제목
    private List<VoteItemResponse> voteItemResponseList;    //투표 항목들 dto list

    public static VoteResponse of(Vote vote) {
        List<VoteItemResponse> voteItemResponseList = vote.getVoteItems().stream()
                .map(VoteItemResponse::of)
                .collect(Collectors.toList());

        return VoteResponse.builder()
                .title(vote.getTitle())
                .voteItemResponseList(voteItemResponseList)
                .build();
    }

}
