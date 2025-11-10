package dev.sergeantfuzzy.calanity.gameplay.abilities.craftist;

import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import dev.sergeantfuzzy.calanity.api.classes.Passive;

/** Chance to refund materials on craft cancel. */
public final class ItemReturnPassive implements Passive {

    @Override
    public String id() {
        return "craftist_item_return";
    }

    @Override
    public String displayName() {
        return "Item Return";
    }

    @Override
    public void execute(AbilityContext context) {
        // Listener-driven passive.
    }
}
