package brama.pressing_api.chat.service;

import brama.pressing_api.chat.domain.UserPresence;
import brama.pressing_api.chat.repo.UserPresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPresenceService {
    private final UserPresenceRepository presenceRepository;

    public UserPresence markOnline(final String userId) {
        UserPresence presence = presenceRepository.findByUserId(userId)
                .orElse(UserPresence.builder().userId(userId).build());
        LocalDateTime now = LocalDateTime.now();
        presence.setOnline(true);
        presence.setLastActiveAt(now);
        presence.setLastSeenAt(now);
        return presenceRepository.save(presence);
    }

    public UserPresence markOffline(final String userId) {
        UserPresence presence = presenceRepository.findByUserId(userId)
                .orElse(UserPresence.builder().userId(userId).build());
        LocalDateTime now = LocalDateTime.now();
        presence.setOnline(false);
        presence.setLastSeenAt(now);
        return presenceRepository.save(presence);
    }

    public UserPresence touch(final String userId) {
        UserPresence presence = presenceRepository.findByUserId(userId)
                .orElse(UserPresence.builder().userId(userId).build());
        LocalDateTime now = LocalDateTime.now();
        presence.setLastActiveAt(now);
        presence.setLastSeenAt(now);
        return presenceRepository.save(presence);
    }

    public boolean isOnline(final String userId) {
        return presenceRepository.findByUserId(userId)
                .map(UserPresence::isOnline)
                .orElse(false);
    }

    public Map<String, UserPresence> findByUserIds(final Collection<String> userIds) {
        Map<String, UserPresence> presenceMap = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return presenceMap;
        }
        presenceRepository.findByUserIdIn(userIds)
                .forEach(presence -> presenceMap.put(presence.getUserId(), presence));
        return presenceMap;
    }
}
