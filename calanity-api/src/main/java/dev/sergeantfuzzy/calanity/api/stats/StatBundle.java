package dev.sergeantfuzzy.calanity.api.stats;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Immutable snapshot of stat values used for HUD + calculations. */
public final class StatBundle {

    private final EnumMap<StatKey, Double> stats;

    private StatBundle(EnumMap<StatKey, Double> stats) {
        this.stats = stats;
    }

    public double get(StatKey key) {
        return stats.getOrDefault(key, 0.0d);
    }

    public Map<StatKey, Double> asMap() {
        return Collections.unmodifiableMap(stats);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final EnumMap<StatKey, Double> working = new EnumMap<>(StatKey.class);

        public Builder set(StatKey key, double value) {
            working.put(key, value);
            return this;
        }

        public Builder add(StatKey key, double delta) {
            working.merge(key, delta, Double::sum);
            return this;
        }

        public StatBundle build() {
            return new StatBundle(new EnumMap<>(working));
        }
    }
}
