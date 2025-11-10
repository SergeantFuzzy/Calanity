package dev.sergeantfuzzy.calanity.integration;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/** Minimal WorldGuard integration placeholder. */
public final class WorldGuardHook {

    private final Plugin plugin;
    private final boolean available;

    public WorldGuardHook(Plugin plugin) {
        this.plugin = plugin;
        this.available = plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isProtected(Location location) {
        // Real implementation would query WG API.
        return false;
    }
}
