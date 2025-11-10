package dev.sergeantfuzzy.calanity.gameplay.abilities.craftist;

import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.Passive;

/**
 * Provides potion-style buffs when holding certain tools. Concrete effects live in listeners.
 */
public final class MagicalArtifactsPassive implements Passive {

    @Override
    public String id() {
        return "craftist_magical_artifacts";
    }

    @Override
    public String displayName() {
        return "Magical Artifacts";
    }

    @Override
    public void execute(AbilityContext context) {
        // Passive aura handled elsewhere.
    }
}
