package dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class WaterAbility implements Ability {

    @Override
    public String id() {
        return "elementalist_water";
    }

    @Override
    public String displayName() {
        return "Water Surge";
    }

    @Override
    public AbilityType type() {
        return AbilityType.ACTIVE;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(10);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = Bukkit.getPlayer(context.playerId());
        if (player != null) {
            player.getWorld().spawnParticle(Particle.SPLASH, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.1);
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 2));
        }
    }
}
