package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.ItemStacks;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Displays top clans for quick reference. */
public final class ClanLeaderboardMenu {

    private final ClanManager clanManager;

    public ClanLeaderboardMenu(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public Inventory build() {
        MenuHolder holder = new MenuHolder(MenuType.CLAN_LEADERBOARD);
        Inventory inv = Bukkit.createInventory(holder, 36, ThemePalette.title("Clan Leaderboard"));
        holder.inventory(inv);
        fill(inv);

        inv.setItem(4, ItemStacks.themed(
                Material.KNOWLEDGE_BOOK,
                ThemePalette.subtitle("Season Highlights"),
                List.of(
                        ThemePalette.muted("Tracking the most powerful clans on Calanity."),
                        ThemePalette.muted("Power increases by victories, quests,"),
                        ThemePalette.muted("and coordinated ability plays."),
                        ThemePalette.accent("Stay active to keep your ranking!"))));

        List<Clan> leaderboard = clanManager.leaderboard(9);
        int slot = 10;
        for (int index = 0; index < leaderboard.size() && slot < 34; index++, slot++) {
            if (slot == 17 || slot == 26) {
                slot += 2;
            }
            Clan clan = leaderboard.get(index);
            inv.setItem(slot, leaderboardEntry(index, clan));
        }

        inv.setItem(27, ItemStacks.backButton("Clan Hub"));
        holder.action(27, "BACK:CLAN_MAIN");

        inv.setItem(31, ItemStacks.themed(
                Material.ENDER_EYE,
                ThemePalette.subtitle("Refresh"),
                List.of(
                        ThemePalette.muted("Updates with the latest standings."),
                        ThemePalette.accent("Click to refresh now."))));
        holder.action(31, "REFRESH_LEADERBOARD");

        inv.setItem(35, ItemStacks.closeButton());
        holder.action(35, "CLOSE");
        return inv;
    }

    private void fill(Inventory inv) {
        ItemStack filler = ItemStacks.fillerPane();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler.clone());
        }
    }

    private ItemStack leaderboardEntry(int position, Clan clan) {
        Material material = switch (position) {
            case 0 -> Material.NETHER_STAR;
            case 1 -> Material.GOLD_BLOCK;
            case 2 -> Material.IRON_BLOCK;
            default -> Material.ORANGE_GLAZED_TERRACOTTA;
        };
        List<Component> lore = new ArrayList<>();
        lore.add(ThemePalette.accent("Power: " + clan.power()).decorate(TextDecoration.BOLD));
        lore.add(ThemePalette.muted("Members: " + clan.members().size()));
        lore.add(ThemePalette.muted("Leaders: " + clan.members().stream()
                .filter(member -> member.role() == ClanRole.LEADER)
                .map(member -> member.name())
                .findFirst()
                .orElse("Unknown")));
        return ItemStacks.themed(material, ThemePalette.subtitle("#" + (position + 1) + " " + clan.displayName()), lore);
    }
}
