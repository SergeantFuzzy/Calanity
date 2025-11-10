package dev.sergeantfuzzy.calanity.gameplay.listeners;

import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.util.Tasker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/** Loads/saves player profiles asynchronously. */
public final class ProfileListener implements Listener {

    private final ProfileService profileService;
    private final HudManager hudManager;
    private final Tasker tasker;

    public ProfileListener(ProfileService profileService, HudManager hudManager, Tasker tasker) {
        this.profileService = profileService;
        this.hudManager = hudManager;
        this.tasker = tasker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        profileService.load(player).thenAccept(profile -> tasker.runSync(() -> hudManager.initialize(player, profile.hudEnabled())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        profileService.save(uuid).thenRun(() -> profileService.unload(uuid));
        hudManager.remove(uuid);
    }
}
