package teamkiim.koffeechat.vote.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveVoteServiceRequest {

    private Long id;
    private List<String> items;
}
