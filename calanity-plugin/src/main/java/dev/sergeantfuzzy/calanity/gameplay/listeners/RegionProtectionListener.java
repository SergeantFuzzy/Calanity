package dev.sergeantfuzzy.calanity.gameplay.listeners;

import dev.sergeantfuzzy.calanity.api.events.AbilityTriggerEvent;
import dev.sergeantfuzzy.calanity.integration.WorldGuardHook;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/** Cancels ability usage in protected regions. */
public final class RegionProtectionListener implements Listener {

    private final WorldGuardHook hook;

    public RegionProtectionListener(WorldGuardHook hook) {
        this.hook = hook;
    }

    @EventHandler
    public void onAbility(AbilityTriggerEvent event) {
        Player player = Bukkit.getPlayer(event.context().playerId());
        if (player == null) {
            return;
        }
        if (hook.isAvailable() && hook.isProtected(player.getLocation())) {
            event.setCancelled(true);
            Messenger.warn(player, "Abilities are disabled in this region.");
        }
    }
}
