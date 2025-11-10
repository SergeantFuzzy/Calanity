package dev.sergeantfuzzy.calanity.gameplay.listeners;

import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import dev.sergeantfuzzy.calanity.domain.clans.ClanPowerService;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/** Awards clan power + stats on player kills. */
public final class CombatListener implements Listener {

    private final ClanPowerService clanPowerService;
    private final StatService statService;
    private final ProfileService profileService;
    private final DataStore dataStore;
    private final HudManager hudManager;

    public CombatListener(ClanPowerService clanPowerService,
                          StatService statService,
                          ProfileService profileService,
                          DataStore dataStore,
                          HudManager hudManager) {
        this.clanPowerService = clanPowerService;
        this.statService = statService;
        this.profileService = profileService;
        this.dataStore = dataStore;
        this.hudManager = hudManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            var killer = event.getEntity().getKiller();
            clanPowerService.handleKill(killer.getUniqueId()).ifPresent(clan -> {
                dataStore.saveClan(clan);
                hudManager.refreshClan(clan);
            });
            var kills = statService.add(killer.getUniqueId(), StatKey.KILLS, 1);
            profileService.profile(killer.getUniqueId()).ifPresent(profile -> profile.stats(kills));
            hudManager.refresh(killer.getUniqueId());
        }
        var deaths = statService.add(event.getEntity().getUniqueId(), StatKey.DEATHS, 1);
        profileService.profile(event.getEntity().getUniqueId()).ifPresent(profile -> profile.stats(deaths));
        hudManager.refresh(event.getEntity().getUniqueId());
    }
}
