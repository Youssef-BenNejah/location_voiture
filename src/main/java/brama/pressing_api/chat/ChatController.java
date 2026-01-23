package brama.pressing_api.chat;

import brama.pressing_api.chat.dto.request.CreateConversationRequest;
import brama.pressing_api.chat.dto.request.EditChatMessageRequest;
import brama.pressing_api.chat.dto.request.SendChatMessageRequest;
import brama.pressing_api.chat.dto.response.ChatConversationResponse;
import brama.pressing_api.chat.dto.response.ChatMessageResponse;
import brama.pressing_api.chat.service.ChatService;
import brama.pressing_api.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Real-time chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/conversations")
    public ResponseEntity<ChatConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            final Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createConversation(request, getUserId(authentication)));
    }

    @GetMapping("/conversations")
    public List<ChatConversationResponse> listConversations(final Authentication authentication) {
        return chatService.listConversations(getUserId(authentication));
    }

    @GetMapping("/conversations/{id}/messages")
    public Page<ChatMessageResponse> listMessages(@PathVariable String id,
                                                  Pageable pageable,
                                                  final Authentication authentication) {
        return chatService.listMessages(id, pageable, getUserId(authentication));
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody SendChatMessageRequest request,
            final Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.sendMessage(request, getUserId(authentication)));
    }

    @PatchMapping("/messages/{id}")
    public ChatMessageResponse editMessage(@PathVariable String id,
                                           @RequestBody EditChatMessageRequest request,
                                           final Authentication authentication) {
        return chatService.editMessage(id, request, getUserId(authentication));
    }

    @DeleteMapping("/messages/{id}")
    public ChatMessageResponse deleteMessage(@PathVariable String id,
                                             final Authentication authentication) {
        return chatService.deleteMessage(id, getUserId(authentication));
    }

    @PostMapping("/conversations/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable String id, final Authentication authentication) {
        chatService.markConversationRead(id, getUserId(authentication));
    }

    @PostMapping("/conversations/{id}/delivered")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markDelivered(@PathVariable String id, final Authentication authentication) {
        chatService.markConversationDelivered(id, getUserId(authentication));
    }

    private String getUserId(final Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
