package dev.sergeantfuzzy.calanity.gameplay.hud;

import dev.sergeantfuzzy.calanity.config.ScoreboardConfig;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.binding.AbilityBindingService;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;
import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import dev.sergeantfuzzy.calanity.api.stats.StatBundle;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import dev.sergeantfuzzy.calanity.ui.text.MiniMsg;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Handles scoreboard text for HUD using scoreboard.yml */
public final class SidebarRenderer {

    private final ClanManager clanManager;
    private final ClassManager classManager;
    private final StatService statService;
    private final AbilityBindingService bindingService;
    private final AbilityManager abilityManager;
    private final ScoreboardConfig scoreboardConfig;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public SidebarRenderer(ClanManager clanManager,
                           ClassManager classManager,
                           StatService statService,
                           AbilityBindingService bindingService,
                           AbilityManager abilityManager,
                           ScoreboardConfig scoreboardConfig) {
        this.clanManager = clanManager;
        this.classManager = classManager;
        this.statService = statService;
        this.bindingService = bindingService;
        this.abilityManager = abilityManager;
        this.scoreboardConfig = scoreboardConfig;
    }

    public void render(Player player) {
        var manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = manager.getNewScoreboard();
        StatBundle stats = statService.get(player.getUniqueId());
        Map<String, String> replacements = placeholderValues(player, stats);
        Objective objective = scoreboard.registerNewObjective("calanity", "dummy",
                colorize(applyPlaceholders(scoreboardConfig.title(), replacements)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Set<String> seen = new HashSet<>();
        int score = scoreboardConfig.lines().size();
        for (String rawLine : scoreboardConfig.lines()) {
            String line = applyPlaceholders(rawLine, replacements);
            if (line.isEmpty()) {
                line = ChatColor.RESET.toString();
            }
            String uniqueLine = ensureUnique(colorize(line), seen);
            objective.getScore(uniqueLine).setScore(score--);
        }
        player.setScoreboard(scoreboard);
    }

    private Map<String, String> placeholderValues(Player player, StatBundle stats) {
        Map<String, String> values = new HashMap<>();
        UUID playerId = player.getUniqueId();
        String playerName = player.getName() != null ? player.getName() : Bukkit.getOfflinePlayer(playerId).getName();
        values.put("%player_name%", playerName == null ? "Player" : playerName);
        Optional<PlayerClass> clazz = classManager.get(playerId);
        values.put("%calanity_class%", clazz.map(PlayerClass::displayName).orElse("None"));
        Optional<Clan> clan = clanManager.findByMember(playerId);
        values.put("%calanity_clan%", clan.map(Clan::displayName).orElse("None"));
        values.put("%calanity_power%", clan.map(c -> String.valueOf(c.power())).orElse("0"));
        values.put("%calanity_clan_members_total%", clan.map(value -> String.valueOf(value.members().size())).orElse("0"));
        values.put("%calanity_clan_members_online%", clan.map(this::countOnlineMembers).orElse("0"));
        values.put("%calanity_clan_leader%", clan.flatMap(this::findLeaderName).orElse("None"));
        values.put("%calanity_kills%", String.valueOf((int) stats.get(StatKey.KILLS)));
        values.put("%calanity_deaths%", String.valueOf((int) stats.get(StatKey.DEATHS)));
        values.put("%calanity_balance%", String.valueOf((int) stats.get(StatKey.BALANCE)));
        int slot = player.getInventory().getHeldItemSlot();
        String abilityName = bindingService.boundAbility(playerId, slot)
                .map(abilityId -> abilityManager.find(abilityId)
                        .map(Ability::displayName)
                        .orElse(abilityId))
                .orElse("None");
        values.put("%calanity_active_ability%", abilityName);
        return values;
    }

    private String countOnlineMembers(Clan clan) {
        long online = clan.members().stream()
                .map(ClanMember::uuid)
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .count();
        return String.valueOf(online);
    }

    private Optional<String> findLeaderName(Clan clan) {
        return clan.members().stream()
                .filter(member -> member.role() == ClanRole.LEADER)
                .map(ClanMember::name)
                .findFirst();
    }

    private String applyPlaceholders(String raw, Map<String, String> replacements) {
        String resolved = raw;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            resolved = resolved.replace(entry.getKey(), entry.getValue());
        }
        return resolved;
    }

    private String ensureUnique(String line, Set<String> seen) {
        String candidate = line;
        int attempt = 0;
        while (seen.contains(candidate) && attempt < 10) {
            candidate = candidate + ChatColor.RESET;
            attempt++;
        }
        seen.add(candidate);
        return candidate;
    }

    private String colorize(String input) {
        return LEGACY.serialize(MiniMsg.parse(input));
    }
}
