package dev.sergeantfuzzy.calanity.api.classes;

import dev.sergeantfuzzy.calanity.api.stats.StatBundle;

import java.util.Collection;

/** Representation of an RPG class (Elfin, Panzer, etc.). */
public interface PlayerClass {

    String id();

    String displayName();

    StatBundle baseStats();

    Collection<Ability> actives();

    Collection<Passive> passives();

    Collection<Ability> clanAbilities();
}
