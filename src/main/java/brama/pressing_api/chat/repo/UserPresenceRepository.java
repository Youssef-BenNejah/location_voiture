package brama.pressing_api.chat.repo;

import brama.pressing_api.chat.domain.UserPresence;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserPresenceRepository extends MongoRepository<UserPresence, String> {
    Optional<UserPresence> findByUserId(String userId);

    List<UserPresence> findByUserIdIn(Collection<String> userIds);
}
