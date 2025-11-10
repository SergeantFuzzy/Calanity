package dev.sergeantfuzzy.calanity.domain.clans;

import dev.sergeantfuzzy.calanity.api.clans.Clan;

import java.util.List;

/** Converts clan data into leaderboard friendly payloads. */
public final class LeaderboardService {

    private final ClanManager clanManager;

    public LeaderboardService(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public List<Clan> top(int limit) {
        return clanManager.leaderboard(limit);
    }
}
