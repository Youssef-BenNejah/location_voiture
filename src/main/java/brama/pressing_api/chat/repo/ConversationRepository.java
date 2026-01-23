package brama.pressing_api.chat.repo;

import brama.pressing_api.chat.domain.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    @Query("{'participant_ids': { $all: [?0, ?1] }}")
    Optional<Conversation> findDirectConversation(String userId, String otherUserId);

    List<Conversation> findByParticipantIdsContaining(String userId);
}
