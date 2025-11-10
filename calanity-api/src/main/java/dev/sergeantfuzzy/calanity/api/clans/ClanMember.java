package dev.sergeantfuzzy.calanity.api.clans;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable member snapshot used in clan lookups and leaderboards.
 */
public final class ClanMember {

    private final UUID uuid;
    private final String name;
    private final ClanRole role;
    private final Instant joinedAt;

    public ClanMember(UUID uuid, String name, ClanRole role, Instant joinedAt) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.name = Objects.requireNonNull(name, "name");
        this.role = Objects.requireNonNull(role, "role");
        this.joinedAt = Objects.requireNonNull(joinedAt, "joinedAt");
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public ClanRole role() {
        return role;
    }

    public Instant joinedAt() {
        return joinedAt;
    }
}
