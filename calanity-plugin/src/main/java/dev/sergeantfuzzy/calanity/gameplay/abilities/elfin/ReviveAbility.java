package dev.sergeantfuzzy.calanity.gameplay.abilities.elfin;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import dev.sergeantfuzzy.calanity.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/** Revives a downed teammate by teleporting them back with full health. */
public final class ReviveAbility implements Ability {

    @Override
    public String id() {
        return "elfin_revive";
    }

    @Override
    public String displayName() {
        return "Revive";
    }

    @Override
    public AbilityType type() {
        return AbilityType.ACTIVE;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofMinutes(2);
    }

    @Override
    public void execute(AbilityContext context) {
        context.clan().ifPresent(clan -> clan.members().stream().findFirst().ifPresent(member -> {
            Player player = Bukkit.getPlayer(member.uuid());
            if (player != null) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                Sounds.playSuccess(player);
            }
        }));
    }
}
