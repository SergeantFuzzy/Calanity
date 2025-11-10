package dev.sergeantfuzzy.calanity.api.clans;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Basic clan descriptor consumed by GUIs, leaderboards, and PlaceholderAPI. */
public final class Clan {

    private final String id;
    private final String displayName;
    private final int power;
    private final Collection<ClanMember> members;

    public Clan(String id, String displayName, int power, Collection<ClanMember> members) {
        this.id = Objects.requireNonNull(id, "id");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.power = power;
        this.members = List.copyOf(members);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public int power() {
        return power;
    }

    public Collection<ClanMember> members() {
        return members;
    }
}
