package dev.sergeantfuzzy.calanity.gameplay.abilities.elfin;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.AbilityType;
import dev.sergeantfuzzy.calanity.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

/** Clan ability: heals online clan members nearby. */
public final class GroupHealAbility implements Ability {

    @Override
    public String id() {
        return "elfin_group_heal";
    }

    @Override
    public String displayName() {
        return "Group Heal";
    }

    @Override
    public AbilityType type() {
        return AbilityType.CLAN;
    }

    @Override
    public Duration cooldown() {
        return Duration.ofSeconds(30);
    }

    @Override
    public void execute(AbilityContext context) {
        context.clan().ifPresent(clan -> {
            for (UUID memberId : clan.members().stream().map(member -> member.uuid()).toList()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null) {
                    member.setHealth(Math.min(member.getMaxHealth(), member.getHealth() + 6));
                    Particles.burst(member, Particle.HEART);
                }
            }
        });
    }
}
