package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.ItemStacks;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Displays members of the player's clan with themed lore + navigation. */
public final class ClanMembersMenu {

    private static final Comparator<ClanMember> ROLE_ORDER = Comparator
            .comparingInt((ClanMember member) -> roleWeight(member.role()))
            .reversed()
            .thenComparing(ClanMember::joinedAt);

    private final ClanManager clanManager;

    public ClanMembersMenu(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public Inventory build(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.CLAN_MEMBERS);
        Inventory inv = Bukkit.createInventory(holder, 36, ThemePalette.title("Clan Roster"));
        holder.inventory(inv);
        fill(inv);

        Clan clan = clanManager.findByMember(player.getUniqueId()).orElse(null);
        if (clan == null) {
            inv.setItem(13, ItemStacks.themed(
                    Material.BOOK,
                    ThemePalette.subtitle("No Clan Yet"),
                    List.of(
                            ThemePalette.muted("Join or create a clan to populate this roster."),
                            ThemePalette.accent("Use /clan create <name> or /clan join <name>."))));
        } else {
            inv.setItem(4, ItemStacks.themed(
                    Material.SHIELD,
                    ThemePalette.subtitle(clan.displayName()),
                    List.of(
                            ThemePalette.accent("Power: " + clan.power()),
                            ThemePalette.muted("Members: " + clan.members().size()),
                            ThemePalette.muted("Captains: " + clan.members().stream()
                                    .filter(member -> member.role().atLeast(ClanRole.CAPTAIN))
                                    .count()))));
            List<ClanMember> sorted = clan.members().stream().sorted(ROLE_ORDER).collect(Collectors.toList());
            int slot = 9;
            for (ClanMember member : sorted) {
                if (slot >= 27) {
                    slot++;
                }
                inv.setItem(slot++, memberEntry(member));
                if (slot % 9 == 0) {
                    slot += 2;
                }
            }
        }

        inv.setItem(27, ItemStacks.backButton("Clan Hub"));
        holder.action(27, "BACK:CLAN_MAIN");
        inv.setItem(35, ItemStacks.closeButton());
        holder.action(35, "CLOSE");
        return inv;
    }

    public void open(Player player) {
        player.openInventory(build(player));
    }

    private void fill(Inventory inv) {
        ItemStack filler = ItemStacks.fillerPane();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler.clone());
        }
    }

    private ItemStack memberEntry(ClanMember member) {
        boolean online = Bukkit.getPlayer(member.uuid()) != null;
        List<Component> lore = List.of(
                ThemePalette.accent("Role: " + prettify(member.role())),
                ThemePalette.muted("Status: " + (online ? "Online" : "Offline")),
                ThemePalette.muted("Joined: " + formatDuration(member.joinedAt())));
        return ItemStacks.playerHead(member.uuid(), member.name(), lore);
    }

    private String formatDuration(Instant joinedAt) {
        Duration duration = Duration.between(joinedAt, Instant.now());
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        if (days > 0) {
            return days + "d " + hours + "h ago";
        }
        long minutes = duration.minusHours(hours).toMinutes();
        if (hours > 0) {
            return hours + "h " + minutes + "m ago";
        }
        return minutes + "m ago";
    }

    private static int roleWeight(ClanRole role) {
        return switch (role) {
            case LEADER -> 3;
            case CAPTAIN -> 2;
            default -> 1;
        };
    }

    private String prettify(ClanRole role) {
        return role.name().substring(0, 1) + role.name().substring(1).toLowerCase(Locale.ROOT);
    }
}
