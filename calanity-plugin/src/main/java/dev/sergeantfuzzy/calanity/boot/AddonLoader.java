package dev.sergeantfuzzy.calanity.boot;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/** Discovers addon jars located in /plugins/Calanity/addons. */
public final class AddonLoader {

    private final Path addonDir;
    private final Logger logger;

    public AddonLoader(Path addonDir, Logger logger) {
        this.addonDir = addonDir;
        this.logger = logger;
    }

    public void load(CalanityAPI api) {
        try {
            Files.createDirectories(addonDir);
            long count = Files.list(addonDir).filter(path -> path.toString().endsWith(".jar")).count();
            logger.info(() -> "Discovered " + count + " addon jars");
        } catch (IOException ex) {
            logger.severe("Failed to scan addons directory: " + ex.getMessage());
        }
    }

    public void unload() {
        logger.info("Unloaded all addons");
    }
}
