package dev.sergeantfuzzy.calanity.api.addon;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;

/**
 * Implemented by addon entry classes. Annotate with {@link dev.sergeantfuzzy.calanity.api.addon.annotations.CalanityEntry}.
 */
public interface CalanityAddon {

    AddonDescription description();

    /**
     * Called after the core plugin is ready. Use this hook to register classes,
     * abilities, clan buffs, etc.
     */
    void onEnable(CalanityAPI api);

    /**
     * Called before the addon is unloaded (including during /calanity reload).
     */
    default void onDisable() {
        // no-op
    }
}
