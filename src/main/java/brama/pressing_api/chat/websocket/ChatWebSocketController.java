package brama.pressing_api.chat.websocket;

import brama.pressing_api.chat.dto.request.ChatReceiptRequest;
import brama.pressing_api.chat.dto.request.SendChatMessageRequest;
import brama.pressing_api.chat.dto.request.TypingEventRequest;
import brama.pressing_api.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void send(@Valid SendChatMessageRequest request, Principal principal) {
        if (principal == null) {
            return;
        }
        chatService.sendMessage(request, principal.getName());
    }

    @MessageMapping("/chat.typing")
    public void typing(@Valid TypingEventRequest request, Principal principal) {
        if (principal == null) {
            return;
        }
        chatService.sendTyping(request.getConversationId(), principal.getName(), request.isTyping());
    }

    @MessageMapping("/chat.read")
    public void read(@Valid ChatReceiptRequest request, Principal principal) {
        if (principal == null) {
            return;
        }
        chatService.markConversationRead(request.getConversationId(), principal.getName());
    }

    @MessageMapping("/chat.delivered")
    public void delivered(@Valid ChatReceiptRequest request, Principal principal) {
        if (principal == null) {
            return;
        }
        chatService.markConversationDelivered(request.getConversationId(), principal.getName());
    }
}
