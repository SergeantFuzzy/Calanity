package dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Duration;

public final class AirAbility implements Ability {

    @Override
    public String id() {
        return "elementalist_air";
    }

    @Override
    public String displayName() {
        return "Air Burst";
    }

    @Override
    public AbilityType type() {
        return AbilityType.ACTIVE;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(8);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = Bukkit.getPlayer(context.playerId());
        if (player != null) {
            player.setVelocity(player.getEyeLocation().getDirection().multiply(1.4).add(new Vector(0, 0.4, 0)));
        }
    }
}
