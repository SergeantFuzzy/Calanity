package dev.sergeantfuzzy.calanity.gameplay.abilities.craftist;

import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.Passive;

/** Grants a chance to duplicate crafted items (handled by listeners). */
public final class DoubleCraftPassive implements Passive {

    @Override
    public String id() {
        return "craftist_double_craft";
    }

    @Override
    public String displayName() {
        return "Double Craft";
    }

    @Override
    public void execute(AbilityContext context) {
        // Passive effect happens inside CraftItemEvent listener.
    }
}
