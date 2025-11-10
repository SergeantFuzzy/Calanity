package dev.sergeantfuzzy.calanity.domain.profile;

import dev.sergeantfuzzy.calanity.api.stats.StatBundle;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents persisted player data (class, clan, stats, HUD preference, etc.).
 */
public final class PlayerProfile {

    private final UUID uuid;
    private String name;
    private String classId = "NONE";
    private String clanId = "";
    private StatBundle stats = StatBundle.builder().build();
    private boolean hudEnabled = true;

    public PlayerProfile(UUID uuid, String name) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.name = Objects.requireNonNull(name, "name");
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String classId() {
        return classId;
    }

    public void classId(String classId) {
        this.classId = classId;
    }

    public String clanId() {
        return clanId;
    }

    public void clanId(String clanId) {
        this.clanId = clanId == null ? "" : clanId;
    }

    public boolean hudEnabled() {
        return hudEnabled;
    }

    public void hudEnabled(boolean hudEnabled) {
        this.hudEnabled = hudEnabled;
    }

    public StatBundle stats() {
        return stats;
    }

    public void stats(StatBundle stats) {
        this.stats = stats;
    }

    public static PlayerProfile fresh(UUID uuid, String name) {
        PlayerProfile profile = new PlayerProfile(uuid, name);
        profile.stats(StatBundle.builder()
                .set(StatKey.KILLS, 0)
                .set(StatKey.DEATHS, 0)
                .set(StatKey.BALANCE, 0)
                .build());
        return profile;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> root = new HashMap<>();
        root.put("uuid", uuid.toString());
        root.put("name", name);
        root.put("class", classId);
        root.put("clan", clanId);
        root.put("hudEnabled", hudEnabled);
        Map<String, Double> statsMap = new HashMap<>();
        stats.asMap().forEach((key, value) -> statsMap.put(key.name(), value));
        root.put("stats", statsMap);
        return root;
    }

    @SuppressWarnings("unchecked")
    public static PlayerProfile deserialize(UUID uuid, Map<String, Object> data) {
        String name = Objects.toString(data.getOrDefault("name", "Unknown"), "Unknown");
        PlayerProfile profile = new PlayerProfile(uuid, name);
        profile.classId(Objects.toString(data.getOrDefault("class", "NONE"), "NONE"));
        profile.clanId(Objects.toString(data.getOrDefault("clan", ""), ""));
        profile.hudEnabled(Boolean.parseBoolean(String.valueOf(data.getOrDefault("hudEnabled", true))));
        Object statsObj = data.get("stats");
        StatBundle.Builder statsBuilder = StatBundle.builder();
        if (statsObj instanceof Map<?, ?> statsMap) {
            statsMap.forEach((key, value) -> {
                try {
                    StatKey statKey = StatKey.valueOf(String.valueOf(key));
                    statsBuilder.set(statKey, value instanceof Number ? ((Number) value).doubleValue() : 0d);
                } catch (IllegalArgumentException ignored) {
                }
            });
        }
        profile.stats(statsBuilder.build());
        return profile;
    }

}
