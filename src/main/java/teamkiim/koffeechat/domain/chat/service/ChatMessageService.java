package teamkiim.koffeechat.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamkiim.koffeechat.domain.chat.domain.message.ChatMessage;
import teamkiim.koffeechat.domain.chat.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.chat.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void save(ChatMessageServiceRequest messageRequest) {

        ChatMessage chatMessage = messageRequest.toEntity();
        chatMessageRepository.save(chatMessage);
    }
}
