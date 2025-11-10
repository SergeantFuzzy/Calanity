package dev.sergeantfuzzy.calanity.gameplay.abilities.panzer;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import dev.sergeantfuzzy.calanity.util.Particles;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Duration;

/** Creates a quick knockback bubble around the player. */
public final class ShieldAbility implements Ability {

    @Override
    public String id() {
        return "panzer_shield";
    }

    @Override
    public String displayName() {
        return "Panzer Shield";
    }

    @Override
    public AbilityType type() {
        return AbilityType.CLAN;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(20);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = playerFromContext(context);
        if (player == null) {
            return;
        }
        player.getNearbyEntities(3, 3, 3).forEach(entity -> {
            Vector push = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.2);
            entity.setVelocity(push);
        });
        Particles.burst(player, Particle.CAMPFIRE_COSY_SMOKE);
    }

    private Player playerFromContext(AbilityContext context) {
        return context.playerId() != null ? org.bukkit.Bukkit.getPlayer(context.playerId()) : null;
    }
}
