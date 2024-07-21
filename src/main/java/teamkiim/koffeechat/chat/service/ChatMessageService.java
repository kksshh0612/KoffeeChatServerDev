package teamkiim.koffeechat.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamkiim.koffeechat.chat.domain.message.ChatMessage;
import teamkiim.koffeechat.chat.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.chat.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void save(ChatMessageServiceRequest messageRequest) {

        ChatMessage chatMessage = messageRequest.toEntity();
        chatMessageRepository.save(chatMessage);
    }
}
