package dev.sergeantfuzzy.calanity.api.classes;

/** Marker interface for passives. */
public interface Passive extends Ability {

    @Override
    default AbilityType type() {
        return AbilityType.PASSIVE;
    }
}
