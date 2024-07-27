package teamkiim.koffeechat.domain.vote.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveVoteRecordRequest {

    @NotEmpty(message="투표할 항목을 선택해 주세요.")
    private List<Long> items;

}
