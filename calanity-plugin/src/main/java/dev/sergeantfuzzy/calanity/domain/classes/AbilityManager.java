package dev.sergeantfuzzy.calanity.domain.classes;

import dev.sergeantfuzzy.calanity.api.classes.Ability;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks all ability instances for triggers/bindings. */
public final class AbilityManager {

    private final Map<String, Ability> abilities = new ConcurrentHashMap<>();

    public void register(Ability ability) {
        abilities.put(ability.id().toLowerCase(), ability);
    }

    public Optional<Ability> find(String id) {
        return Optional.ofNullable(abilities.get(id.toLowerCase()));
    }

    public Collection<Ability> all() {
        return abilities.values();
    }
}
