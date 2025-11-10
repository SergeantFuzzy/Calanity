package dev.sergeantfuzzy.calanity.storage.yaml;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.domain.profile.PlayerProfile;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import dev.sergeantfuzzy.calanity.storage.util.ClanCodec;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

/** Simple YAML backed store – great for dev servers. */
public final class YamlStore implements DataStore {

    private final Path playersDir;
    private final Path clansDir;
    private final ExecutorService executor;

    public YamlStore(Path dataFolder) {
        this.playersDir = dataFolder.resolve("data").resolve("players");
        this.clansDir = dataFolder.resolve("clans");
        this.executor = Executors.newFixedThreadPool(2, new NamedThreadFactory());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(playersDir);
            Files.createDirectories(clansDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create data directory", ex);
        }
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }

    @Override
    public CompletableFuture<Void> savePlayer(PlayerProfile profile) {
        return CompletableFuture.runAsync(() -> {
            Path file = playersDir.resolve(profile.uuid() + ".yml");
            YamlConfiguration yaml = new YamlConfiguration();
            profile.serialize().forEach(yaml::set);
            try {
                yaml.save(file.toFile());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<PlayerProfile> loadPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Path file = playersDir.resolve(uuid + ".yml");
            if (!Files.exists(file)) {
                return null;
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
            Map<String, Object> values = new HashMap<>();
            yaml.getKeys(false).forEach(key -> values.put(key, yaml.get(key)));
            return PlayerProfile.deserialize(uuid, values);
        }, executor);
    }

    @Override
    public CompletableFuture<Map<String, Clan>> loadClans() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Clan> clans = new HashMap<>();
            try {
                if (!Files.exists(clansDir)) {
                    Files.createDirectories(clansDir);
                }
                try (Stream<Path> stream = Files.list(clansDir)) {
                    stream.filter(path -> path.toString().endsWith(".yml")).forEach(path -> {
                        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path.toFile());
                        Map<String, Object> values = new HashMap<>();
                        yaml.getKeys(false).forEach(key -> values.put(key, yaml.get(key)));
                        Clan clan = ClanCodec.fromMap(values);
                        clans.put(clan.id(), clan);
                    });
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return clans;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> saveClan(Clan clan) {
        return CompletableFuture.runAsync(() -> {
            Path file = clansDir.resolve(clan.id() + ".yml");
            YamlConfiguration yaml = new YamlConfiguration();
            ClanCodec.toMap(clan).forEach(yaml::set);
            try {
                yaml.save(file.toFile());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteClan(String clanId) {
        return CompletableFuture.runAsync(() -> {
            Path file = clansDir.resolve(clanId + ".yml");
            try {
                Files.deleteIfExists(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    private static final class NamedThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "calanity-yaml-" + counter++);
            thread.setDaemon(true);
            return thread;
        }
    }
}
