package dev.sergeantfuzzy.calanity.domain.clans;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;
import dev.sergeantfuzzy.calanity.api.clans.ClanService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** In-memory clan registry – persistence handled by DataStore implementations. */
public final class ClanManager implements ClanService {

    private final Map<String, Clan> clans = new ConcurrentHashMap<>();
    private final Map<UUID, String> memberships = new ConcurrentHashMap<>();

    @Override
    public Optional<Clan> findById(String id) {
        return Optional.ofNullable(clans.get(key(id)));
    }

    @Override
    public Optional<Clan> findByMember(UUID playerId) {
        String clanId = memberships.get(playerId);
        return clanId == null ? Optional.empty() : findById(clanId);
    }

    @Override
    public Clan createClan(String id, String displayName, UUID ownerId, String ownerName) {
        ClanMember member = new ClanMember(ownerId, ownerName, ClanRole.LEADER, Instant.now());
        Clan clan = new Clan(id, displayName, 0, List.of(member));
        save(clan);
        return clan;
    }

    @Override
    public void disband(String id) {
        Clan removed = clans.remove(key(id));
        if (removed != null) {
            removed.members().forEach(member -> memberships.remove(member.uuid()));
        }
    }

    @Override
    public List<Clan> leaderboard(int limit) {
        return clans.values().stream()
                .sorted((a, b) -> Integer.compare(b.power(), a.power()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Collection<Clan> all() {
        return List.copyOf(clans.values());
    }

    public synchronized void save(Clan clan) {
        Clan previous = clans.put(key(clan.id()), clan);
        if (previous != null) {
            previous.members().forEach(member -> memberships.remove(member.uuid()));
        }
        clan.members().forEach(member -> memberships.put(member.uuid(), clan.id()));
    }

    public Clan updatePower(String clanId, int newPower) {
        Clan existing = clans.get(key(clanId));
        if (existing == null) {
            return null;
        }
        Clan updated = new Clan(existing.id(), existing.displayName(), newPower, new ArrayList<>(existing.members()));
        save(updated);
        return updated;
    }

    public Optional<ClanMember> member(UUID uuid) {
        return findByMember(uuid).flatMap(clan -> clan.members().stream()
                .filter(member -> member.uuid().equals(uuid))
                .findFirst());
    }

    public Clan addMember(String clanId, ClanMember member) {
        Clan clan = clans.get(key(clanId));
        if (clan == null) {
            return null;
        }
        List<ClanMember> members = new ArrayList<>(clan.members());
        members.removeIf(existing -> existing.uuid().equals(member.uuid()));
        members.add(member);
        Clan updated = new Clan(clan.id(), clan.displayName(), clan.power(), members);
        save(updated);
        return updated;
    }

    public Clan removeMember(UUID playerId) {
        String clanId = memberships.remove(playerId);
        if (clanId == null) {
            return null;
        }
        Clan clan = clans.get(key(clanId));
        if (clan == null) {
            return null;
        }
        List<ClanMember> members = clan.members().stream()
                .filter(member -> !member.uuid().equals(playerId))
                .collect(Collectors.toList());
        Clan updated = new Clan(clan.id(), clan.displayName(), clan.power(), members);
        save(updated);
        return updated;
    }

    public boolean isMember(UUID playerId) {
        return memberships.containsKey(playerId);
    }

    public Optional<Clan> findByDisplayName(String displayName) {
        return clans.values().stream()
                .filter(clan -> clan.displayName().equalsIgnoreCase(displayName))
                .findFirst();
    }

    public Clan updateRole(UUID playerId, ClanRole newRole) {
        String clanId = memberships.get(playerId);
        if (clanId == null) {
            return null;
        }
        Clan clan = clans.get(key(clanId));
        if (clan == null) {
            return null;
        }
        List<ClanMember> members = clan.members().stream()
                .map(member -> member.uuid().equals(playerId)
                        ? new ClanMember(member.uuid(), member.name(), newRole, member.joinedAt())
                        : member)
                .collect(Collectors.toList());
        Clan updated = new Clan(clan.id(), clan.displayName(), clan.power(), members);
        save(updated);
        return updated;
    }

    public void clear() {
        clans.clear();
        memberships.clear();
    }

    private String key(String id) {
        return id.toLowerCase(Locale.ROOT);
    }
}
