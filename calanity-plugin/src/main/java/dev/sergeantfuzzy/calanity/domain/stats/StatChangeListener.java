package dev.sergeantfuzzy.calanity.domain.stats;

import dev.sergeantfuzzy.calanity.api.stats.StatKey;

import java.util.UUID;

@FunctionalInterface
public interface StatChangeListener {
    void onStatChange(UUID playerId, StatKey key);
}
