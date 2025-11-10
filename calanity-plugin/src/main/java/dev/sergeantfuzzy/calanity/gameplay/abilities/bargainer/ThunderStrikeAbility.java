package dev.sergeantfuzzy.calanity.gameplay.abilities.bargainer;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/** Retaliates against attackers via lightning. */
public final class ThunderStrikeAbility implements Ability {

    @Override
    public String id() {
        return "bargainer_thunder_strike";
    }

    @Override
    public String displayName() {
        return "Thunder Strike";
    }

    @Override
    public AbilityType type() {
        return AbilityType.CLAN;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(25);
    }

    @Override
    public void execute(AbilityContext context) {
        Player player = Bukkit.getPlayer(context.playerId());
        if (player != null && player.getLastDamageCause() != null) {
            var damager = player.getLastDamageCause().getEntity();
            if (damager != null) {
                damager.getWorld().strikeLightningEffect(damager.getLocation());
            }
        }
    }
}
