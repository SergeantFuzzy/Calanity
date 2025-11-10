package dev.sergeantfuzzy.calanity.ui.holograms;

import dev.sergeantfuzzy.calanity.domain.clans.LeaderboardService;

import java.util.List;
import java.util.stream.Collectors;

/** Builds hologram lines for top clans. */
public final class ClanTopHologram {

    private final LeaderboardService leaderboardService;

    public ClanTopHologram(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    public List<String> lines() {
        return leaderboardService.top(5).stream()
                .map(clan -> clan.displayName() + " - " + clan.power())
                .collect(Collectors.toList());
    }
}
