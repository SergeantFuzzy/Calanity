package dev.sergeantfuzzy.calanity.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

/** Loads scoreboard.yml which defines HUD title + lines. */
public final class ScoreboardConfig {

    private final JavaPlugin plugin;
    private String title;
    private List<String> lines;

    public ScoreboardConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        File file = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!file.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.title = config.getString("title", "<gold>Calanity</gold>");
        List<String> configuredLines = config.getStringList("lines");
        if (configuredLines == null || configuredLines.isEmpty()) {
            configuredLines = List.of("<gray>Configure scoreboard.yml</gray>");
        }
        this.lines = Collections.unmodifiableList(List.copyOf(configuredLines));
    }

    public String title() {
        return title;
    }

    public List<String> lines() {
        return lines;
    }
}
