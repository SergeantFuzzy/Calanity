package dev.sergeantfuzzy.calanity.domain.clans;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** High-level clan operations with persistence + invites. */
public final class ClanFacade {

    private final ClanManager clanManager;
    private final DataStore dataStore;
    private final ProfileService profileService;
    private final int maxMembers;
    private final HudManager hudManager;
    private final Map<UUID, Map<String, ClanInvite>> invites = new ConcurrentHashMap<>();

    public ClanFacade(ClanManager clanManager,
                      DataStore dataStore,
                      ProfileService profileService,
                      int maxMembers,
                      HudManager hudManager) {
        this.clanManager = clanManager;
        this.dataStore = dataStore;
        this.profileService = profileService;
        this.maxMembers = maxMembers;
        this.hudManager = hudManager;
    }

    public Optional<Clan> createClan(Player creator, String displayName) {
        if (clanManager.isMember(creator.getUniqueId())) {
            return Optional.empty();
        }
        String id = slug(displayName);
        if (clanManager.findById(id).isPresent()) {
            return Optional.empty();
        }
        Clan clan = clanManager.createClan(id, displayName, creator.getUniqueId(), creator.getName());
        dataStore.saveClan(clan);
        profileService.setClan(creator.getUniqueId(), clan.id());
        refreshClan(clan.id());
        return Optional.of(clan);
    }

    public boolean invite(Player inviter, Player target) {
        Optional<ClanMember> inviterMember = clanManager.member(inviter.getUniqueId());
        if (inviterMember.isEmpty()) {
            return false;
        }
        if (!inviterMember.get().role().atLeast(ClanRole.CAPTAIN)) {
            return false;
        }
        Clan clan = clanManager.findByMember(inviter.getUniqueId()).orElse(null);
        if (clan == null || clan.members().size() >= maxMembers) {
            return false;
        }
        invites.computeIfAbsent(target.getUniqueId(), id -> new ConcurrentHashMap<>())
                .put(clan.id(), new ClanInvite(clan.id(), inviter.getUniqueId(), System.currentTimeMillis() + 300_000L));
        Messenger.success(target, "You have been invited to " + clan.displayName() + "! Use /clan accept " + clan.displayName());
        return true;
    }

    public boolean join(Player player, String clanName) {
        if (clanManager.isMember(player.getUniqueId())) {
            return false;
        }
        Optional<Clan> clanOpt = clanManager.findByDisplayName(clanName);
        if (clanOpt.isEmpty()) {
            clanOpt = clanManager.findById(slug(clanName));
            if (clanOpt.isEmpty()) {
                return false;
            }
        }
        Clan clan = clanOpt.get();
        if (clan.members().size() >= maxMembers) {
            return false;
        }
        ClanMember member = new ClanMember(player.getUniqueId(), player.getName(), ClanRole.MEMBER, Instant.now());
        Clan updated = clanManager.addMember(clan.id(), member);
        profileService.setClan(player.getUniqueId(), clan.id());
        profileService.save(player.getUniqueId());
        dataStore.saveClan(updated);
        refreshClan(clan.id());
        return true;
    }

    public boolean acceptInvite(Player player, String clanName) {
        Map<String, ClanInvite> playerInvites = invites.getOrDefault(player.getUniqueId(), Map.of());
        if (playerInvites.isEmpty()) {
            return false;
        }
        ClanInvite invite = clanName == null
                ? playerInvites.values().stream().findFirst().orElse(null)
                : playerInvites.get(slug(clanName));
        if (invite == null) {
            return false;
        }
        if (invite.expired()) {
            playerInvites.remove(invite.clanId());
            return false;
        }
        Clan clan = clanManager.findById(invite.clanId()).orElse(null);
        if (clan == null || clan.members().size() >= maxMembers) {
            return false;
        }
        clanManager.removeMember(player.getUniqueId());
        ClanMember member = new ClanMember(player.getUniqueId(), player.getName(), ClanRole.MEMBER, Instant.now());
        Clan updated = clanManager.addMember(clan.id(), member);
        dataStore.saveClan(updated);
        profileService.setClan(player.getUniqueId(), clan.id());
        profileService.save(player.getUniqueId());
        invites.get(player.getUniqueId()).remove(invite.clanId());
        cleanupInvites(player.getUniqueId());
        refreshClan(clan.id());
        return true;
    }

    public boolean denyInvite(Player player, String clanName) {
        Map<String, ClanInvite> playerInvites = invites.get(player.getUniqueId());
        if (playerInvites == null || playerInvites.isEmpty()) {
            return false;
        }
        if (clanName == null) {
            playerInvites.clear();
            cleanupInvites(player.getUniqueId());
            return true;
        }
        boolean removed = playerInvites.remove(slug(clanName)) != null;
        cleanupInvites(player.getUniqueId());
        refreshPlayer(player.getUniqueId());
        return removed;
    }

    public boolean leaveClan(Player player) {
        Optional<Clan> clanOpt = clanManager.findByMember(player.getUniqueId());
        if (clanOpt.isEmpty()) {
            return false;
        }
        Clan clan = clanOpt.get();
        ClanMember member = clan.members().stream()
                .filter(m -> m.uuid().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
        if (member == null) {
            return false;
        }
        Clan updated = clanManager.removeMember(player.getUniqueId());
        profileService.setClan(player.getUniqueId(), "");
        profileService.save(player.getUniqueId());
        if (updated == null || updated.members().isEmpty()) {
            disband(clan.id());
            return true;
        }
        if (member.role() == ClanRole.LEADER) {
            promoteNextLeader(updated.id());
        } else {
            dataStore.saveClan(updated);
        }
        refreshPlayer(player.getUniqueId());
        refreshClan(updated.id());
        return true;
    }

    public boolean disband(Player player) {
        Optional<Clan> clanOpt = clanManager.findByMember(player.getUniqueId());
        if (clanOpt.isEmpty()) {
            return false;
        }
        Clan clan = clanOpt.get();
        ClanMember member = clan.members().stream()
                .filter(m -> m.uuid().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
        if (member == null || member.role() != ClanRole.LEADER) {
            return false;
        }
        disband(clan.id());
        return true;
    }

    public boolean promote(Player actor, OfflinePlayer target) {
        Optional<Clan> clanOpt = clanManager.findByMember(actor.getUniqueId());
        if (clanOpt.isEmpty() || clanManager.findByMember(target.getUniqueId()).isEmpty()) {
            return false;
        }
        Clan clan = clanOpt.get();
        ClanMember actorMember = clan.members().stream()
                .filter(member -> member.uuid().equals(actor.getUniqueId()))
                .findFirst()
                .orElse(null);
        ClanMember targetMember = clan.members().stream()
                .filter(member -> member.uuid().equals(target.getUniqueId()))
                .findFirst()
                .orElse(null);
        if (actorMember == null || targetMember == null) {
            return false;
        }
        if (!actorMember.role().atLeast(ClanRole.LEADER)) {
            return false;
        }
        ClanRole newRole = targetMember.role() == ClanRole.MEMBER ? ClanRole.CAPTAIN : ClanRole.LEADER;
        Clan updated = clanManager.updateRole(target.getUniqueId(), newRole);
        if (newRole == ClanRole.LEADER) {
            updated = clanManager.updateRole(actor.getUniqueId(), ClanRole.CAPTAIN);
        }
        if (updated != null) {
            dataStore.saveClan(updated);
        }
        refreshClan(clan.id());
        return true;
    }

    public List<Clan> leaderboard() {
        return clanManager.leaderboard(10);
    }

    private void promoteNextLeader(String clanId) {
        Clan clan = clanManager.findById(clanId).orElse(null);
        if (clan == null || clan.members().isEmpty()) {
            return;
        }
        ClanMember next = clan.members().stream().findFirst().orElse(null);
        if (next == null) {
            return;
        }
        Clan updated = clanManager.updateRole(next.uuid(), ClanRole.LEADER);
        if (updated != null) {
            dataStore.saveClan(updated);
        }
    }

    private void disband(String clanId) {
        clanManager.findById(clanId).ifPresent(clan -> {
            clanManager.disband(clanId);
            dataStore.deleteClan(clanId);
            clan.members().forEach(member -> {
                profileService.setClan(member.uuid(), "");
                profileService.save(member.uuid());
                refreshPlayer(member.uuid());
            });
        });
    }

    public List<String> pendingInvites(UUID playerId, String prefix) {
        String search = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return invites.getOrDefault(playerId, Map.of()).keySet().stream()
                .map(clanManager::findById)
                .flatMap(Optional::stream)
                .map(Clan::displayName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(search))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    public List<String> clanMemberSuggestions(UUID actorId, String prefix) {
        return clanManager.findByMember(actorId)
                .map(clan -> TabSuggestions.clanMemberNames(clan, prefix == null ? "" : prefix))
                .orElse(List.of());
    }

    private String slug(String input) {
        String value = input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return value.isEmpty() ? "clan" + System.currentTimeMillis() : value;
    }

    private void cleanupInvites(UUID uuid) {
        invites.computeIfPresent(uuid, (id, map) -> map.isEmpty() ? null : map);
    }

    private void refreshClan(String clanId) {
        if (hudManager == null || clanId == null) {
            return;
        }
        clanManager.findById(clanId).ifPresent(hudManager::refreshClan);
    }

    private void refreshPlayer(UUID playerId) {
        if (hudManager != null) {
            hudManager.refresh(playerId);
        }
    }

    private record ClanInvite(String clanId, UUID inviter, long expiresAt) {
        boolean expired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
