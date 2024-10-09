package teamkiim.koffeechat.domain.post.community.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.community.dto.request.ModifyCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.vote.dto.request.ModifyVoteServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCommunityPostRequest {

    ModifyCommunityPostInfoRequest modifyCommunityPostInfoRequest;
    ModifyVoteRequest modifyVoteRequest;

    public ModifyCommunityPostServiceRequest toPostServiceRequest() {
        return ModifyCommunityPostServiceRequest.builder()
                .title(this.modifyCommunityPostInfoRequest.getTitle())
                .bodyContent(this.modifyCommunityPostInfoRequest.getBodyContent())
                .fileIdList(this.getModifyCommunityPostInfoRequest().getFileIdList())
                .tagContentList(this.modifyCommunityPostInfoRequest.getTagContentList())
                .build();
    }

    public ModifyVoteServiceRequest toVoteServiceRequest() {
        if (this.modifyVoteRequest == null) {
            return null;
        }

        return ModifyVoteServiceRequest.builder()
                .items(this.modifyVoteRequest.getItems())
                .build();
    }
}
