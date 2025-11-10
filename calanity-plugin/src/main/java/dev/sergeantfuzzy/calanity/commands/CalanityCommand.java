package dev.sergeantfuzzy.calanity.commands;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import dev.sergeantfuzzy.calanity.config.ConfigManager;
import dev.sergeantfuzzy.calanity.config.ReloadService;
import dev.sergeantfuzzy.calanity.debug.DebugLogger;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.profile.PlayerProfile;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.integration.PlaceholderAPIHook;
import dev.sergeantfuzzy.calanity.integration.VaultEconomyHook;
import dev.sergeantfuzzy.calanity.integration.WorldGuardHook;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** /calanity debug|reload|profile|hud|about */
public final class CalanityCommand implements CommandExecutor, TabCompleter {

    private final ReloadService reloadService;
    private final DebugLogger debugLogger;
    private final ClassManager classManager;
    private final StatService statService;
    private final ProfileService profileService;
    private final DataStore dataStore;
    private final ClanManager clanManager;
    private final HudManager hudManager;
    private final Plugin plugin;
    private final ConfigManager configManager;
    private final PlaceholderAPIHook placeholderHook;
    private final WorldGuardHook worldGuardHook;
    private final VaultEconomyHook vaultHook;

    public CalanityCommand(ReloadService reloadService,
                           DebugLogger debugLogger,
                           ClassManager classManager,
                           StatService statService,
                           ProfileService profileService,
                           DataStore dataStore,
                           ClanManager clanManager,
                           HudManager hudManager,
                           Plugin plugin,
                           ConfigManager configManager,
                           PlaceholderAPIHook placeholderHook,
                           WorldGuardHook worldGuardHook,
                           VaultEconomyHook vaultHook) {
        this.reloadService = reloadService;
        this.debugLogger = debugLogger;
        this.classManager = classManager;
        this.statService = statService;
        this.profileService = profileService;
        this.dataStore = dataStore;
        this.clanManager = clanManager;
        this.hudManager = hudManager;
        this.plugin = plugin;
        this.configManager = configManager;
        this.placeholderHook = placeholderHook;
        this.worldGuardHook = worldGuardHook;
        this.vaultHook = vaultHook;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Messenger.info(sender, "Use /calanity debug|reload|profile|hud");
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "debug" -> handleDebug(sender);
            case "profile" -> handleProfile(sender, args);
            case "hud" -> handleHud(sender, args);
            case "about" -> handleAbout(sender);
            default -> {
                Messenger.warn(sender, "Unknown subcommand. Try /calanity debug|reload|profile|hud");
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        reloadService.reload();
        Messenger.success(sender, "Calanity configuration, storage, and HUD refreshed.");
        return true;
    }

    private boolean handleDebug(CommandSender sender) {
        debugLogger.setEnabled(!debugLogger.enabled());
        Messenger.info(sender, "Debug mode toggled " + (debugLogger.enabled() ? "on" : "off") + ".");
        return true;
    }

    private boolean handleProfile(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/calanity profile <player>");
            return true;
        }
        var target = Bukkit.getOfflinePlayer(args[1]);
        PlayerProfile profile = profileService.profile(target.getUniqueId())
                .orElseGet(() -> dataStore.loadPlayer(target.getUniqueId()).join());
        if (profile == null) {
            Messenger.warn(sender, "No profile data for that player.");
            return true;
        }
        Optional<Clan> clan = profile.clanId().isEmpty() ? Optional.empty() : clanManager.findById(profile.clanId());
        String className = classManager.findById(profile.classId()).map(clazz -> clazz.displayName()).orElse("None");
        Messenger.list(sender, "Profile: " + target.getName(), List.of(
                MessengerLine.primary("Class", className),
                MessengerLine.primary("Clan", clan.map(Clan::displayName).orElse("None")),
                MessengerLine.primary("Kills", String.valueOf((int) profile.stats().get(StatKey.KILLS))),
                MessengerLine.primary("Deaths", String.valueOf((int) profile.stats().get(StatKey.DEATHS)))
        ));
        return true;
    }

    private boolean handleHud(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messenger.warn(sender, "Only in-game players can toggle the HUD.");
            return true;
        }
        if (args.length < 2 || !args[1].equalsIgnoreCase("toggle")) {
            Messenger.usage(sender, "/calanity hud toggle");
            return true;
        }
        boolean enabled = hudManager.toggle(player);
        profileService.profile(player.getUniqueId()).ifPresent(profile -> profile.hudEnabled(enabled));
        profileService.save(player.getUniqueId());
        Messenger.success(sender, "HUD " + (enabled ? "enabled" : "disabled") + ".");
        return true;
    }

    private boolean handleAbout(CommandSender sender) {
        String version = plugin.getDescription().getVersion();
        String authors = String.join(", ", plugin.getDescription().getAuthors());
        String storage = configManager.storageType();
        Messenger.list(sender, "About Calanity", List.of(
                MessengerLine.primary("Version", version),
                MessengerLine.primary("Developers", authors),
                MessengerLine.primary("Storage", storage),
                MessengerLine.primary("PlaceholderAPI", hookStatus(placeholderHook != null)),
                MessengerLine.primary("WorldGuard", hookStatus(worldGuardHook != null && worldGuardHook.isAvailable())),
                MessengerLine.primary("Vault", hookStatus(vaultHook != null && vaultHook.economy().isPresent()))
        ));
        return true;
    }

    private String hookStatus(boolean active) {
        return active ? "Hooked" : "Missing";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Stream.of("debug", "reload", "profile", "hud", "about"), args[0]);
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "profile" -> {
                if (args.length == 2) {
                    return TabSuggestions.onlinePlayers(args[1]);
                }
            }
            case "hud" -> {
                if (args.length == 2) {
                    return filter(Stream.of("toggle"), args[1]);
                }
            }
            default -> {
            }
        }
        return List.of();
    }

    private List<String> filter(Stream<String> source, String prefix) {
        String search = prefix == null ? "" : prefix.toLowerCase();
        return source
                .filter(option -> option.startsWith(search))
                .sorted()
                .toList();
    }

    private static final class MessengerLine {
        private static Component primary(String label, String value) {
            return ThemePalette.subtitle(label + ": ").append(ThemePalette.muted(value));
        }
    }
}
