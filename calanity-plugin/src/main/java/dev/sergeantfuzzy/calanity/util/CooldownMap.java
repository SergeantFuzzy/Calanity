package dev.sergeantfuzzy.calanity.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Thread-safe cooldown helper keyed by player + key string. */
public final class CooldownMap {

    private final Map<String, Instant> cooldowns = new ConcurrentHashMap<>();

    private String key(UUID playerId, String abilityId) {
        return playerId + ":" + abilityId;
    }

    public boolean isCooling(UUID playerId, String abilityId) {
        Instant until = cooldowns.get(key(playerId, abilityId));
        return until != null && Instant.now().isBefore(until);
    }

    public Duration remaining(UUID playerId, String abilityId) {
        Instant until = cooldowns.get(key(playerId, abilityId));
        if (until == null) {
            return Duration.ZERO;
        }
        long seconds = Math.max(0, until.getEpochSecond() - Instant.now().getEpochSecond());
        return Duration.ofSeconds(seconds);
    }

    public void apply(UUID playerId, String abilityId, Duration duration) {
        cooldowns.put(key(playerId, abilityId), Instant.now().plus(duration));
    }

    public void clear(UUID playerId) {
        cooldowns.keySet().removeIf(key -> key.startsWith(playerId.toString()));
    }
}
