package dev.sergeantfuzzy.calanity.domain.classes.binding;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Stores hotbar bindings for abilities. */
public final class AbilityBindingService {

    private final Map<UUID, Map<Integer, String>> bindings = new ConcurrentHashMap<>();

    public void bind(UUID playerId, int slot, String abilityId) {
        bindings.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>()).put(slot, abilityId);
    }

    public Optional<String> boundAbility(UUID playerId, int slot) {
        return Optional.ofNullable(bindings.getOrDefault(playerId, Map.of()).get(slot));
    }

    public Map<Integer, String> bindings(UUID playerId) {
        return Collections.unmodifiableMap(bindings.getOrDefault(playerId, Map.of()));
    }
}
