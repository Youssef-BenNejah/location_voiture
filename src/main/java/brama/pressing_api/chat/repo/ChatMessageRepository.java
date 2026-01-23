package brama.pressing_api.chat.repo;

import brama.pressing_api.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findByConversationIdOrderByCreatedDateDesc(String conversationId, Pageable pageable);

    List<ChatMessage> findByConversationIdAndRecipientId(String conversationId, String recipientId);
}
