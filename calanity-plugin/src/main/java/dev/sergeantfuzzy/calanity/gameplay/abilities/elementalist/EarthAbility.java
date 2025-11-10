package dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class EarthAbility implements Ability {

    @Override
    public String id() {
        return "elementalist_earth";
    }

    @Override
    public String displayName() {
        return "Earth Bulwark";
    }

    @Override
    public AbilityType type() {
        return AbilityType.ACTIVE;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(15);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = Bukkit.getPlayer(context.playerId());
        if (player != null) {
            player.getLocation().getBlock().setType(Material.MOSS_BLOCK);
        }
    }
}
