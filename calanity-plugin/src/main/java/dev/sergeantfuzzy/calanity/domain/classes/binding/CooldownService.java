package dev.sergeantfuzzy.calanity.domain.classes.binding;

import dev.sergeantfuzzy.calanity.util.CooldownMap;

import java.time.Duration;
import java.util.UUID;

/** Wraps CooldownMap with nicer semantics. */
public final class CooldownService {

    private final CooldownMap cooldowns = new CooldownMap();

    public boolean isCooling(UUID playerId, String abilityId) {
        return cooldowns.isCooling(playerId, abilityId);
    }

    public void apply(UUID playerId, String abilityId, Duration duration) {
        cooldowns.apply(playerId, abilityId, duration);
    }
}
