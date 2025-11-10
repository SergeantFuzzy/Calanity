package dev.sergeantfuzzy.calanity;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import dev.sergeantfuzzy.calanity.api.CalanityProvider;
import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.clans.ClanService;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import dev.sergeantfuzzy.calanity.api.placeholders.PlaceholderSource;
import dev.sergeantfuzzy.calanity.boot.AddonLoader;
import dev.sergeantfuzzy.calanity.config.ConfigManager;
import dev.sergeantfuzzy.calanity.config.ReloadService;
import dev.sergeantfuzzy.calanity.config.ScoreboardConfig;
import dev.sergeantfuzzy.calanity.debug.DebugLogger;
import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.domain.clans.ClanPowerService;
import dev.sergeantfuzzy.calanity.domain.clans.LeaderboardService;
import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.classes.SimplePlayerClass;
import dev.sergeantfuzzy.calanity.domain.classes.binding.AbilityBindingService;
import dev.sergeantfuzzy.calanity.domain.classes.binding.CooldownService;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.gameplay.abilities.bargainer.ThunderStrikeAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.craftist.DoubleCraftPassive;
import dev.sergeantfuzzy.calanity.gameplay.abilities.craftist.ItemReturnPassive;
import dev.sergeantfuzzy.calanity.gameplay.abilities.craftist.MagicalArtifactsPassive;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elfin.GroupHealAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elfin.ReviveAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist.AirAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist.EarthAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist.FireAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.elementalist.WaterAbility;
import dev.sergeantfuzzy.calanity.gameplay.abilities.panzer.ShieldAbility;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.gameplay.hud.SidebarRenderer;
import dev.sergeantfuzzy.calanity.gameplay.listeners.AbilityHudListener;
import dev.sergeantfuzzy.calanity.gameplay.listeners.AbilityTriggerListener;
import dev.sergeantfuzzy.calanity.gameplay.listeners.CombatListener;
import dev.sergeantfuzzy.calanity.gameplay.listeners.ProfileListener;
import dev.sergeantfuzzy.calanity.gameplay.listeners.RegionProtectionListener;
import dev.sergeantfuzzy.calanity.integration.PlaceholderAPIHook;
import dev.sergeantfuzzy.calanity.integration.VaultEconomyHook;
import dev.sergeantfuzzy.calanity.integration.WorldGuardHook;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import dev.sergeantfuzzy.calanity.storage.StorageType;
import dev.sergeantfuzzy.calanity.storage.sql.MysqlDriver;
import dev.sergeantfuzzy.calanity.storage.sql.SqliteDriver;
import dev.sergeantfuzzy.calanity.storage.yaml.YamlStore;
import dev.sergeantfuzzy.calanity.ui.gui.ClanLeaderboardMenu;
import dev.sergeantfuzzy.calanity.ui.gui.ClanMainMenu;
import dev.sergeantfuzzy.calanity.ui.gui.ClanMembersMenu;
import dev.sergeantfuzzy.calanity.ui.gui.ClanSettingsMenu;
import dev.sergeantfuzzy.calanity.ui.gui.ClassSelectMenu;
import dev.sergeantfuzzy.calanity.ui.gui.MenuListener;
import dev.sergeantfuzzy.calanity.ui.text.MiniMsg;
import dev.sergeantfuzzy.calanity.util.Tasker;
import dev.sergeantfuzzy.calanity.commands.CalanityCommand;
import dev.sergeantfuzzy.calanity.commands.clan.ClanCommand;
import dev.sergeantfuzzy.calanity.commands.clazz.ClassCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CalanityPlugin extends JavaPlugin implements PlaceholderSource {

    private ConfigManager configManager;
    private ReloadService reloadService;
    private ClanManager clanManager;
    private ClanPowerService clanPowerService;
    private LeaderboardService leaderboardService;
    private ClassManager classManager;
    private AbilityManager abilityManager;
    private AbilityBindingService bindingService;
    private CooldownService cooldownService;
    private StatService statService;
    private SidebarRenderer sidebarRenderer;
    private HudManager hudManager;
    private ScoreboardConfig scoreboardConfig;
    private Tasker tasker;
    private PlaceholderAPIHook placeholderHook;
    private WorldGuardHook worldGuardHook;
    private VaultEconomyHook vaultHook;
    private DebugLogger debugLogger;
    private AddonLoader addonLoader;
    private DataStore dataStore;
    private ProfileService profileService;
    private ClanFacade clanFacade;
    private ClanMainMenu clanMainMenu;
    private ClanMembersMenu clanMembersMenu;
    private ClanSettingsMenu clanSettingsMenu;
    private ClanLeaderboardMenu clanLeaderboardMenu;
    private ClassSelectMenu classSelectMenu;
    private final Map<String, Ability> builtinAbilities = new HashMap<>();
    private CalanityAPI api;

    @Override
    public void onLoad() {
        new CalanityBootstrap().onLoad(this);
    }

    @Override
    public void onEnable() {
        this.tasker = new Tasker(this);
        this.configManager = new ConfigManager(this);
        this.configManager.reload();
        this.debugLogger = new DebugLogger(getLogger());
        this.clanManager = new ClanManager();
        this.clanPowerService = new ClanPowerService(clanManager);
        this.leaderboardService = new LeaderboardService(clanManager);
        this.classManager = new ClassManager();
        this.abilityManager = new AbilityManager();
        this.bindingService = new AbilityBindingService();
        this.cooldownService = new CooldownService();
        this.statService = new StatService();
        this.scoreboardConfig = new ScoreboardConfig(this);
        this.sidebarRenderer = new SidebarRenderer(clanManager, classManager, statService, bindingService, abilityManager, scoreboardConfig);
        this.hudManager = new HudManager(sidebarRenderer, tasker);
        this.statService.onChange((playerId, key) -> {
            if (key == null || key == StatKey.KILLS || key == StatKey.DEATHS || key == StatKey.BALANCE) {
                hudManager.refresh(playerId);
            }
        });
        this.clanMainMenu = new ClanMainMenu(clanManager);
        this.clanMembersMenu = new ClanMembersMenu(clanManager);
        this.clanSettingsMenu = new ClanSettingsMenu(clanManager);
        this.clanLeaderboardMenu = new ClanLeaderboardMenu(clanManager);
        this.classSelectMenu = new ClassSelectMenu(classManager);
        this.worldGuardHook = new WorldGuardHook(this);
        this.vaultHook = new VaultEconomyHook();
        this.vaultHook.hook();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderHook = new PlaceholderAPIHook(this, clanManager, classManager, statService);
            this.placeholderHook.register();
        } else {
            this.placeholderHook = null;
            getLogger().info("PlaceholderAPI not found; skipping placeholder expansion registration.");
        }
        registerAbilities();
        registerDefaultClasses();
        reloadModules();
        this.reloadService = new ReloadService(configManager, this::reloadModules);
        this.api = new PluginApi();
        CalanityProvider.register(api);
        this.addonLoader = new AddonLoader(getDataFolder().toPath().resolve("addons"), getLogger());
        this.addonLoader.load(api);
        registerListeners();
        registerCommands();
        logLifecycleBanner("ONLINE");
    }

    @Override
    public void onDisable() {
        if (addonLoader != null) {
            addonLoader.unload();
        }
        if (profileService != null) {
            profileService.allProfiles().keySet().forEach(uuid -> profileService.save(uuid).join());
        }
        if (dataStore != null) {
            dataStore.shutdown();
        }
        if (api != null) {
            CalanityProvider.unregister(api);
        }
        logLifecycleBanner("OFFLINE");
    }

    private void registerAbilities() {
        register(new GroupHealAbility());
        register(new ReviveAbility());
        register(new ShieldAbility());
        register(new ThunderStrikeAbility());
        register(new FireAbility());
        register(new WaterAbility());
        register(new AirAbility());
        register(new EarthAbility());
        register(new DoubleCraftPassive());
        register(new ItemReturnPassive());
        register(new MagicalArtifactsPassive());
    }

    private void register(Ability ability) {
        abilityManager.register(ability);
        builtinAbilities.put(ability.id(), ability);
    }

    private void registerDefaultClasses() {
        classManager.register(new SimplePlayerClass(
                "elfin",
                "Elfin",
                baseStats(4, 0, 0),
                java.util.List.of(ability("elfin_revive")),
                java.util.List.of(),
                java.util.List.of(ability("elfin_group_heal"))
        ));
        classManager.register(new SimplePlayerClass(
                "panzer",
                "Panzer",
                baseStats(8, 20, 0),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(ability("panzer_shield"))
        ));
        classManager.register(new SimplePlayerClass(
                "bargainer",
                "Bargainer",
                baseStats(0, 0, 5),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(ability("bargainer_thunder_strike"))
        ));
        classManager.register(new SimplePlayerClass(
                "craftist",
                "Craftist",
                baseStats(0, 0, 0),
                java.util.List.of(),
                java.util.List.of(
                        (dev.sergeantfuzzy.calanity.api.classes.Passive) ability("craftist_double_craft"),
                        (dev.sergeantfuzzy.calanity.api.classes.Passive) ability("craftist_item_return"),
                        (dev.sergeantfuzzy.calanity.api.classes.Passive) ability("craftist_magical_artifacts")
                ),
                java.util.List.of()
        ));
        classManager.register(new SimplePlayerClass(
                "elementalist",
                "Elementalist",
                baseStats(0, 5, 5),
                java.util.List.of(
                        ability("elementalist_fire"),
                        ability("elementalist_water"),
                        ability("elementalist_air"),
                        ability("elementalist_earth")
                ),
                java.util.List.of(),
                java.util.List.of()
        ));
    }

    private dev.sergeantfuzzy.calanity.api.stats.StatBundle baseStats(double health, double defense, double damage) {
        return dev.sergeantfuzzy.calanity.api.stats.StatBundle.builder()
                .set(dev.sergeantfuzzy.calanity.api.stats.StatKey.HEALTH, health)
                .set(dev.sergeantfuzzy.calanity.api.stats.StatKey.DEFENSE, defense)
                .set(dev.sergeantfuzzy.calanity.api.stats.StatKey.DAMAGE, damage)
                .build();
    }

    private Ability ability(String id) {
        return Objects.requireNonNull(builtinAbilities.get(id), "Ability not registered: " + id);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new ProfileListener(profileService, hudManager, tasker), this);
        pm.registerEvents(new CombatListener(clanPowerService, statService, profileService, dataStore, hudManager), this);
        pm.registerEvents(new AbilityTriggerListener(bindings(), abilityManager, cooldownService, clanManager, classManager, statService, worldGuardHook), this);
        pm.registerEvents(new RegionProtectionListener(worldGuardHook), this);
        pm.registerEvents(new MenuListener(clanMainMenu, clanMembersMenu, clanSettingsMenu, clanLeaderboardMenu, classSelectMenu, classManager, profileService, hudManager), this);
        pm.registerEvents(new AbilityHudListener(hudManager), this);
    }

    private AbilityBindingService bindings() {
        return bindingService;
    }

    private void registerCommands() {
        var calanity = getCommand("calanity");
        if (calanity != null) {
            var calanityCommand = new CalanityCommand(
                    reloadService,
                    debugLogger,
                    classManager,
                    statService,
                    profileService,
                    dataStore,
                    clanManager,
                    hudManager,
                    this,
                    configManager,
                    placeholderHook,
                    worldGuardHook,
                    vaultHook
            );
            calanity.setExecutor(calanityCommand);
            calanity.setTabCompleter(calanityCommand);
        }
        var clan = getCommand("clan");
        if (clan != null) {
            ClanCommand command = new ClanCommand(clanManager, clanFacade, clanMainMenu);
            clan.setExecutor(command);
            clan.setTabCompleter(command);
        }
        var clazz = getCommand("class");
        if (clazz != null) {
            ClassCommand command = new ClassCommand(classManager, abilityManager, bindingService, profileService, classSelectMenu, hudManager);
            clazz.setExecutor(command);
            clazz.setTabCompleter(command);
        }
    }

    private void reloadModules() {
        if (scoreboardConfig != null) {
            scoreboardConfig.reload();
        }
        this.debugLogger.setEnabled(configManager.debugEnabled());
        if (dataStore != null) {
            dataStore.shutdown();
        }
        StorageType storageType = StorageType.from(configManager.storageType());
        Path dataPath = getDataFolder().toPath();
        this.dataStore = switch (storageType) {
            case YAML -> new YamlStore(dataPath);
            case SQLITE -> new SqliteDriver(dataPath);
            case MYSQL -> new MysqlDriver(
                    configManager.mysqlHost(),
                    configManager.mysqlPort(),
                    configManager.mysqlDatabase(),
                    configManager.mysqlUsername(),
                    configManager.mysqlPassword()
            );
        };
        dataStore.init();
        clanPowerService.setKillValue(configManager.clanKillPower());
        if (profileService == null) {
            profileService = new ProfileService(dataStore, classManager, statService);
        } else {
            profileService.dataStore(dataStore);
        }
        this.clanFacade = new ClanFacade(clanManager, dataStore, profileService, configManager.clanMaxMembers(), hudManager);
        loadPersistedClans();
        Bukkit.getOnlinePlayers().forEach(hudManager::refresh);
    }

    @Override
    public Optional<String> resolve(UUID playerId, String placeholderId) {
        if (placeholderHook == null) {
            return Optional.empty();
        }
        return placeholderHook.resolve(playerId, placeholderId);
    }

    private void loadPersistedClans() {
        clanManager.clear();
        dataStore.loadClans().join().values().forEach(clanManager::save);
    }

    private final class PluginApi implements CalanityAPI {
        @Override
        public ClanService clans() {
            return clanManager;
        }

        @Override
        public dev.sergeantfuzzy.calanity.api.classes.registry.ClassRegistry classes() {
            return classManager;
        }

        @Override
        public PlaceholderSource placeholders() {
            return CalanityPlugin.this;
        }
    }

    private void logLifecycleBanner(String status) {
        var console = getServer().getConsoleSender();
        String version = getDescription().getVersion();
        String authors = String.join(", ", getDescription().getAuthors());
        String storage = configManager != null ? configManager.storageType() : "unknown";
        boolean placeholderReady = placeholderHook != null;
        boolean worldGuardReady = worldGuardHook != null && worldGuardHook.isAvailable();
        boolean vaultReady = vaultHook != null && vaultHook.economy().isPresent();
        String placeholderColor = placeholderReady ? "#fbbf24" : "#ef4444";
        String worldGuardColor = worldGuardReady ? "#fbbf24" : "#ef4444";
        String vaultColor = vaultReady ? "#fbbf24" : "#ef4444";
        String statusColor = "ONLINE".equalsIgnoreCase(status) ? "#fbbf24" : "#ef4444";
        List<String> lines = List.of(
                "<#1f1f1f>========================================</#1f1f1f>",
                "<#d97706><bold>Calanity MMORPG</bold></#d97706> <#d1d5db>v" + version + "</#d1d5db>",
                "<#b45309>Status:</#b45309> <" + statusColor + ">" + status + "</" + statusColor + ">",
                "<#b45309>Developers:</#b45309> <#d1d5db>" + authors + "</#d1d5db>",
                "<#b45309>Storage:</#b45309> <#d1d5db>" + storage + "</#d1d5db>",
                "<#b45309>PlaceholderAPI:</#b45309> <" + placeholderColor + ">" + (placeholderReady ? "Hooked" : "Missing") + "</" + placeholderColor + ">",
                "<#b45309>WorldGuard:</#b45309> <" + worldGuardColor + ">" + (worldGuardReady ? "Hooked" : "Missing") + "</" + worldGuardColor + ">",
                "<#b45309>Vault:</#b45309> <" + vaultColor + ">" + (vaultReady ? "Hooked" : "Missing") + "</" + vaultColor + ">",
                "<#1f1f1f>========================================</#1f1f1f>"
        );
        lines.forEach(line -> console.sendMessage(MiniMsg.parse(line)));
    }
}
