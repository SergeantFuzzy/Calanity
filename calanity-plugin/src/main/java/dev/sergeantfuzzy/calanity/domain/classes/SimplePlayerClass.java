package dev.sergeantfuzzy.calanity.domain.classes;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.Passive;
import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import dev.sergeantfuzzy.calanity.api.stats.StatBundle;

import java.util.Collection;
import java.util.List;

/** Simple immutable {@link PlayerClass} implementation. */
public final class SimplePlayerClass implements PlayerClass {

    private final String id;
    private final String displayName;
    private final StatBundle baseStats;
    private final Collection<Ability> actives;
    private final Collection<Passive> passives;
    private final Collection<Ability> clanAbilities;

    public SimplePlayerClass(String id,
                             String displayName,
                             StatBundle baseStats,
                             Collection<Ability> actives,
                             Collection<Passive> passives,
                             Collection<Ability> clanAbilities) {
        this.id = id;
        this.displayName = displayName;
        this.baseStats = baseStats;
        this.actives = List.copyOf(actives);
        this.passives = List.copyOf(passives);
        this.clanAbilities = List.copyOf(clanAbilities);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public StatBundle baseStats() {
        return baseStats;
    }

    @Override
    public Collection<Ability> actives() {
        return actives;
    }

    @Override
    public Collection<Passive> passives() {
        return passives;
    }

    @Override
    public Collection<Ability> clanAbilities() {
        return clanAbilities;
    }
}
