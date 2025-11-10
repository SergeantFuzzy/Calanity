package dev.sergeantfuzzy.calanity;

import dev.sergeantfuzzy.calanity.boot.StartupVerifier;
import org.bukkit.plugin.java.JavaPlugin;

/** Called from {@link CalanityPlugin#onLoad()} to run env checks. */
public final class CalanityBootstrap {

    private final StartupVerifier verifier = new StartupVerifier();

    public void onLoad(JavaPlugin plugin) {
        verifier.verify(plugin.getLogger());
    }
}
