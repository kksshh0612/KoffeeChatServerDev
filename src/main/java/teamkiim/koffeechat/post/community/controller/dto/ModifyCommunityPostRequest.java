package teamkiim.koffeechat.post.community.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.post.community.service.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.vote.service.dto.request.ModifyVoteServiceRequest;
import teamkiim.koffeechat.vote.service.dto.request.SaveVoteServiceRequest;

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
        return ModifyVoteServiceRequest.builder()
                .title(this.modifyVoteRequest.getTitle())
                .items(this.modifyVoteRequest.getItems())
                .build();
    }
}
