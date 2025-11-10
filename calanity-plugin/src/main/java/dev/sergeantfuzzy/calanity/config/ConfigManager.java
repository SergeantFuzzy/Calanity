package dev.sergeantfuzzy.calanity.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/** Handles config.yml reload + typed accessors. */
public final class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration raw() {
        return config;
    }

    public String storageType() {
        return config.getString(ConfigKeys.STORAGE_TYPE, "YAML");
    }

    public boolean debugEnabled() {
        return config.getBoolean(ConfigKeys.DEBUG_ENABLED, false);
    }

    public int clanMaxMembers() {
        return config.getInt(ConfigKeys.CLAN_MAX_MEMBERS, 10);
    }

    public int clanKillPower() {
        return config.getInt("clans.kill-power", 30);
    }

    public String mysqlHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int mysqlPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String mysqlDatabase() {
        return config.getString("mysql.database", "calanity");
    }

    public String mysqlUsername() {
        return config.getString("mysql.username", "root");
    }

    public String mysqlPassword() {
        return config.getString("mysql.password", "");
    }
}
