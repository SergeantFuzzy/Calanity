package dev.sergeantfuzzy.calanity.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/** Utility for spawning thematic particles. */
public final class Particles {

    private Particles() {
    }

    public static void burst(Player player, Particle particle) {
        Location loc = player.getLocation().clone().add(0, 1, 0);
        player.getWorld().spawnParticle(particle, loc, 25, 0.3, 0.3, 0.3, 0.01);
    }
}
