package dev.sergeantfuzzy.calanity.config;

import java.util.Objects;

/** Orchestrates safe reload steps so /calanity reload stays deterministic. */
public final class ReloadService {

    private final ConfigManager configManager;
    private final Runnable afterReload;

    public ReloadService(ConfigManager configManager, Runnable afterReload) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.afterReload = Objects.requireNonNull(afterReload, "afterReload");
    }

    public void reload() {
        configManager.reload();
        afterReload.run();
    }
}
