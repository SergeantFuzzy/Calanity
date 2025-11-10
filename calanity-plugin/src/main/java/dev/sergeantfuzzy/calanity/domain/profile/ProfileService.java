package dev.sergeantfuzzy.calanity.domain.profile;

import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads, caches, and persists player profiles.
 */
public final class ProfileService {

    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private DataStore dataStore;
    private final ClassManager classManager;
    private final StatService statService;

    public ProfileService(DataStore dataStore, ClassManager classManager, StatService statService) {
        this.dataStore = dataStore;
        this.classManager = classManager;
        this.statService = statService;
    }

    public void dataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public CompletableFuture<PlayerProfile> load(Player player) {
        return dataStore.loadPlayer(player.getUniqueId()).thenApply(profile -> {
            PlayerProfile effective = profile != null ? profile : PlayerProfile.fresh(player.getUniqueId(), player.getName());
            effective.name(player.getName());
            profiles.put(player.getUniqueId(), effective);
            if (!"NONE".equalsIgnoreCase(effective.classId())) {
                classManager.findById(effective.classId()).ifPresent(clazz -> classManager.assign(player.getUniqueId(), clazz));
            }
            statService.put(player.getUniqueId(), effective.stats());
            return effective;
        });
    }

    public void unload(UUID uuid) {
        profiles.remove(uuid);
    }

    public CompletableFuture<Void> save(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) {
            return CompletableFuture.completedFuture(null);
        }
        profile.stats(statService.get(uuid));
        return dataStore.savePlayer(profile);
    }

    public Optional<PlayerProfile> profile(UUID uuid) {
        return Optional.ofNullable(profiles.get(uuid));
    }

    public void setClass(UUID uuid, String classId) {
        profile(uuid).ifPresent(profile -> profile.classId(classId));
    }

    public void setClan(UUID uuid, String clanId) {
        profile(uuid).ifPresent(profile -> profile.clanId(clanId));
    }

    public Map<UUID, PlayerProfile> allProfiles() {
        return Map.copyOf(profiles);
    }
}
