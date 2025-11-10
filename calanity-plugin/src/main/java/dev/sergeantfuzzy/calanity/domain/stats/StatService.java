package dev.sergeantfuzzy.calanity.domain.stats;

import dev.sergeantfuzzy.calanity.api.stats.StatBundle;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Tracks runtime player stats shown in HUDs and menus. */
public final class StatService {

    private final Map<UUID, StatBundle> stats = new ConcurrentHashMap<>();
    private final List<StatChangeListener> listeners = new CopyOnWriteArrayList<>();

    public StatBundle get(UUID playerId) {
        return stats.getOrDefault(playerId, StatBundle.builder().build());
    }

    public void put(UUID playerId, StatBundle bundle) {
        stats.put(playerId, bundle);
        notifyChange(playerId, null);
    }

    public StatBundle add(UUID playerId, StatKey key, double amount) {
        StatBundle current = get(playerId);
        StatBundle.Builder builder = StatBundle.builder();
        current.asMap().forEach((k, value) -> builder.set(k, value));
        builder.add(key, amount);
        StatBundle updated = builder.build();
        stats.put(playerId, updated);
        notifyChange(playerId, key);
        return updated;
    }

    public void onChange(StatChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyChange(UUID playerId, StatKey key) {
        listeners.forEach(listener -> listener.onStatChange(playerId, key));
    }
}
