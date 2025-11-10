package dev.sergeantfuzzy.calanity.domain.clans;

import dev.sergeantfuzzy.calanity.api.clans.Clan;

import java.util.Optional;
import java.util.UUID;

/** Handles clan power accumulation. */
public final class ClanPowerService {

    private final ClanManager clanManager;
    private int killValue = 30;

    public ClanPowerService(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void setKillValue(int killValue) {
        this.killValue = killValue;
    }

    public Optional<Clan> handleKill(UUID killer) {
        return clanManager.findByMember(killer).map(clan -> {
            int updated = clan.power() + killValue;
            return clanManager.updatePower(clan.id(), updated);
        });
    }
}
