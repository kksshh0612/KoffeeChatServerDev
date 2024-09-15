package teamkiim.koffeechat.domain.vote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyVoteServiceRequest {

    private List<String> items;             //투표 항목

    public SaveVoteServiceRequest toSaveVoteServiceRequest() {
        return SaveVoteServiceRequest.builder()
                .items(this.items)
                .build();
    }
}
