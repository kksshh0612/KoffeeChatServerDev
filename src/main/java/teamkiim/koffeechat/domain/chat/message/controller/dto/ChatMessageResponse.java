package teamkiim.koffeechat.domain.chat.message.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.message.domain.MessageType;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.message.service.ChatMessageService;
import teamkiim.koffeechat.domain.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickName;
    private String content;
    private String profileImagePath;
    private String profileImageName;

    public static ChatMessageResponse of(ChatMessageServiceRequest chatMessageServiceRequest, Member sendMember,
                                         Long chatRoomId){

        return ChatMessageResponse.builder()
                .messageType(chatMessageServiceRequest.getMessageType())
                .chatRoomId(chatRoomId)
                .senderId(sendMember.getId())
                .senderNickName(sendMember.getNickname())
                .content(chatMessageServiceRequest.getContent())
                .profileImagePath(sendMember.getProfileImagePath())
                .profileImageName(sendMember.getProfileImageName())
                .build();
    }
}
