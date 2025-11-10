package dev.sergeantfuzzy.calanity.api.clans;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * High level clan actions used by GUIs, commands, and addons.
 */
public interface ClanService {

    Optional<Clan> findById(String id);

    Optional<Clan> findByMember(UUID playerId);

    Clan createClan(String id, String displayName, UUID ownerId, String ownerName);

    void disband(String id);

    List<Clan> leaderboard(int limit);
}
