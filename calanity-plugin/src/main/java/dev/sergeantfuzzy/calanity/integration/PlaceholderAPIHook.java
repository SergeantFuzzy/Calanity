package dev.sergeantfuzzy.calanity.integration;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/** PlaceholderAPI expansion exposing %calanity_*% identifiers. */
public final class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;
    private final ClanManager clanManager;
    private final ClassManager classManager;
    private final StatService statService;

    public PlaceholderAPIHook(Plugin plugin, ClanManager clanManager, ClassManager classManager, StatService statService) {
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.classManager = classManager;
        this.statService = statService;
    }

    @Override
    public String getIdentifier() {
        return "calanity";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        return resolve(player.getUniqueId(), params).orElse("");
    }

    public Optional<String> resolve(UUID playerId, String params) {
        if (playerId == null) {
            return Optional.empty();
        }
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerId);
        return Optional.ofNullable(resolveInternal(offlinePlayer, params));
    }

    private String resolveInternal(OfflinePlayer player, String params) {
        if (player == null || player.getUniqueId() == null) {
            return "";
        }
        String key = params.toLowerCase(Locale.ROOT);
        return switch (key) {
            case "class" -> classManager.get(player.getUniqueId()).map(cls -> cls.displayName()).orElse("None");
            case "clan" -> clanManager.findByMember(player.getUniqueId()).map(Clan::displayName).orElse("None");
            case "power" -> clanManager.findByMember(player.getUniqueId()).map(clan -> String.valueOf(clan.power())).orElse("0");
            case "kills" -> String.valueOf((int) statService.get(player.getUniqueId()).get(StatKey.KILLS));
            case "deaths" -> String.valueOf((int) statService.get(player.getUniqueId()).get(StatKey.DEATHS));
            case "balance" -> String.valueOf((int) statService.get(player.getUniqueId()).get(StatKey.BALANCE));
            default -> resolveStat(key, player);
        };
    }

    private String resolveStat(String key, OfflinePlayer player) {
        Optional<StatKey> statKey = parseStatKey(key);
        return statKey.map(value -> String.valueOf(statService.get(player.getUniqueId()).get(value))).orElse("");
    }

    private Optional<StatKey> parseStatKey(String key) {
        try {
            return Optional.of(StatKey.valueOf(key.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
