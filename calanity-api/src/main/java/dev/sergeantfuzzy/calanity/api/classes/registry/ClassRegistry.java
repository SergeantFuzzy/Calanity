package dev.sergeantfuzzy.calanity.api.classes.registry;

import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;

import java.util.Collection;
import java.util.Optional;

/** Registry of all player classes (core + addons). */
public interface ClassRegistry {

    Optional<PlayerClass> findById(String id);

    void register(PlayerClass playerClass);

    void unregister(String id);

    Collection<PlayerClass> all();
}
