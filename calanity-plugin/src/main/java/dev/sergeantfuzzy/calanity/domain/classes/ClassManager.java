package dev.sergeantfuzzy.calanity.domain.classes;

import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import dev.sergeantfuzzy.calanity.api.classes.registry.ClassRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Stores registered classes and current player choices. */
public final class ClassManager implements ClassRegistry {

    private final Map<String, PlayerClass> classes = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerClass> selections = new ConcurrentHashMap<>();

    @Override
    public Optional<PlayerClass> findById(String id) {
        return Optional.ofNullable(classes.get(id.toLowerCase()));
    }

    @Override
    public void register(PlayerClass playerClass) {
        classes.put(playerClass.id().toLowerCase(), playerClass);
    }

    @Override
    public void unregister(String id) {
        classes.remove(id.toLowerCase());
    }

    @Override
    public Collection<PlayerClass> all() {
        return classes.values();
    }

    public void assign(UUID playerId, PlayerClass playerClass) {
        selections.put(playerId, playerClass);
    }

    public Optional<PlayerClass> get(UUID playerId) {
        return Optional.ofNullable(selections.get(playerId));
    }

    public void clear(UUID playerId) {
        selections.remove(playerId);
    }

    public Map<UUID, PlayerClass> assignments() {
        return Map.copyOf(selections);
    }
}
