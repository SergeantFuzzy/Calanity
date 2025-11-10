package dev.sergeantfuzzy.calanity.gameplay.listeners;

import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

/** Refreshes HUD when players change hotbar slots to show new bound abilities. */
public final class AbilityHudListener implements Listener {

    private final HudManager hudManager;

    public AbilityHudListener(HudManager hudManager) {
        this.hudManager = hudManager;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        hudManager.refresh(event.getPlayer());
    }
}
