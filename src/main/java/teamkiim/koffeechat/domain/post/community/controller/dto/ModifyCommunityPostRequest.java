package teamkiim.koffeechat.domain.post.community.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.community.service.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.vote.service.dto.request.ModifyVoteServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCommunityPostRequest {

    ModifyCommunityPostInfoRequest modifyCommunityPostInfoRequest;
    ModifyVoteRequest modifyVoteRequest;

    public ModifyCommunityPostServiceRequest toPostServiceRequest(){
        return ModifyCommunityPostServiceRequest.builder()
                .id(this.modifyCommunityPostInfoRequest.getId())
                .title(this.modifyCommunityPostInfoRequest.getTitle())
                .bodyContent(this.modifyCommunityPostInfoRequest.getBodyContent())
                .build();
    }

    public ModifyVoteServiceRequest toVoteServiceRequest() {
        if (this.modifyVoteRequest != null) {
            return ModifyVoteServiceRequest.builder()
                    .title(this.modifyVoteRequest.getTitle())
                    .items(this.modifyVoteRequest.getItems())
                    .build();
        }
        return null;
    }
}
