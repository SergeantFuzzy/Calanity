package dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class FireAbility implements Ability {

    @Override
    public String id() {
        return "elementalist_fire";
    }

    @Override
    public String displayName() {
        return "Fire Jet";
    }

    @Override
    public AbilityType type() {
        return AbilityType.ACTIVE;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(12);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = Bukkit.getPlayer(context.playerId());
        if (player != null) {
            player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.05);
        }
    }
}
