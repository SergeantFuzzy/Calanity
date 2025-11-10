package dev.sergeantfuzzy.calanity.api.classes;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.stats.StatBundle;

import java.util.Optional;
import java.util.UUID;

/**
 * Context data passed to abilities when triggered.
 */
public interface AbilityContext {

    UUID playerId();

    Optional<Clan> clan();

    Optional<PlayerClass> playerClass();

    StatBundle stats();
}
