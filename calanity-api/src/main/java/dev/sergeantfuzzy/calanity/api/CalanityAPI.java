package dev.sergeantfuzzy.calanity.api;

import dev.sergeantfuzzy.calanity.api.classes.registry.ClassRegistry;
import dev.sergeantfuzzy.calanity.api.clans.ClanService;
import dev.sergeantfuzzy.calanity.api.placeholders.PlaceholderSource;

/**
 * Entry point exposed to addons at runtime. The plugin registers the provider
 * during {@link org.bukkit.plugin.java.JavaPlugin#onEnable()}.
 */
public interface CalanityAPI {

    /**
     * @return clan service with lookup and leaderboard helpers.
     */
    ClanService clans();

    /**
     * @return registry that stores all base and addon classes/abilities.
     */
    ClassRegistry classes();

    /**
     * @return placeholder bridge for PlaceholderAPI expansion data.
     */
    PlaceholderSource placeholders();

    /**
     * Convenience accessor that proxies to {@link CalanityProvider}.
     */
    static CalanityAPI provider() {
        return CalanityProvider.require();
    }
}
