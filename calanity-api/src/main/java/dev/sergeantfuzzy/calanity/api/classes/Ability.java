package dev.sergeantfuzzy.calanity.api.classes;

import java.time.Duration;

/**
 * Base contract for any ability (active, passive, or clan). Addons can extend this.
 */
public interface Ability {

    String id();

    String displayName();

    AbilityType type();

    default Duration cooldown() {
        return Duration.ZERO;
    }

    void execute(AbilityContext context);
}
