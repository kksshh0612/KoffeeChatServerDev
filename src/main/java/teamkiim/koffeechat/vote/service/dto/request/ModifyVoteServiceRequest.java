package teamkiim.koffeechat.vote.service.dto.request;

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

    private String title;                   //투표 제목
    private List<String> items;             //투표 항목

    public SaveVoteServiceRequest toSaveVoteServiceRequest() {
        return SaveVoteServiceRequest.builder()
                .title(this.title)
                .items(this.items)
                .build();
    }
}
