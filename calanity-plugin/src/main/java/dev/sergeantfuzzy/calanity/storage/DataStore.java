package dev.sergeantfuzzy.calanity.storage;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.domain.profile.PlayerProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Common contract for YAML / SQL data stores. */
public interface DataStore {

    void init();

    void shutdown();

    CompletableFuture<Void> savePlayer(PlayerProfile profile);

    CompletableFuture<PlayerProfile> loadPlayer(UUID uuid);

    CompletableFuture<Map<String, Clan>> loadClans();

    CompletableFuture<Void> saveClan(Clan clan);

    CompletableFuture<Void> deleteClan(String clanId);
}
