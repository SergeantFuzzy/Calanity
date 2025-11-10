package dev.sergeantfuzzy.calanity.domain.classes;

import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.stats.StatBundle;

import java.util.Optional;
import java.util.UUID;

/** Simple immutable {@link AbilityContext} implementation. */
public record DefaultAbilityContext(UUID playerId, Optional<Clan> clan, Optional<PlayerClass> playerClass, StatBundle stats) implements AbilityContext {
}
