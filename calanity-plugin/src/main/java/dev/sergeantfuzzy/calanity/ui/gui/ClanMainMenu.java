package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.ItemStacks;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Themed clan hub entry point with guidance + quick actions. */
public final class ClanMainMenu {

    private final ClanManager clanManager;

    public ClanMainMenu(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void open(Player player) {
        player.openInventory(build(player));
    }

    public Inventory build(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.CLAN_MAIN);
        Inventory inv = Bukkit.createInventory(holder, 36, ThemePalette.title("Clan Hub"));
        holder.inventory(inv);
        fill(inv);

        inv.setItem(10, ItemStacks.themed(
                Material.BOOK,
                ThemePalette.subtitle("About Calanity"),
                List.of(
                        ThemePalette.muted("Modular MMORPG framework built for Paper 1.21+"),
                        ThemePalette.accent("• Clans earn buffs, power, and leaderboard clout."),
                        ThemePalette.accent("• Classes unlock actives, passives, and clan abilities."),
                        ThemePalette.muted("Drop addons into /plugins/Calanity/addons for more."))));

        inv.setItem(11, ItemStacks.themed(
                Material.PLAYER_HEAD,
                ThemePalette.subtitle("Clan Roster"),
                List.of(
                        ThemePalette.muted("Browse members, roles, and join dates."),
                        ThemePalette.accent("Click to open your roster."))));
        holder.action(11, "OPEN_MEMBERS");

        inv.setItem(12, clanSummary(player));
        holder.action(12, "OPEN_SETTINGS");

        inv.setItem(13, ItemStacks.themed(
                Material.END_CRYSTAL,
                ThemePalette.subtitle("Power Rankings"),
                leaderboardLore()));
        holder.action(13, "OPEN_LEADERBOARD");

        inv.setItem(15, ItemStacks.themed(
                Material.WRITABLE_BOOK,
                ThemePalette.subtitle("Commands & Tips"),
                List.of(
                        ThemePalette.accent("/clan create <name>"),
                        ThemePalette.accent("/clan invite <player>"),
                        ThemePalette.accent("/calanity reload"),
                        ThemePalette.muted("Click to view the complete guide in chat."))));
        holder.action(15, "SHOW_COMMANDS");

        inv.setItem(16, ItemStacks.themed(
                Material.NETHER_STAR,
                ThemePalette.subtitle("Classes & Abilities"),
                List.of(
                        ThemePalette.muted("Choose a specialty, bind abilities, and manage cooldowns."),
                        ThemePalette.accent("Click to open the class selector."))));
        holder.action(16, "OPEN_CLASS_MENU");

        inv.setItem(22, ItemStacks.themed(
                Material.COMPASS,
                ThemePalette.subtitle("Getting Started"),
                List.of(
                        ThemePalette.muted("1. Select a class that matches your playstyle."),
                        ThemePalette.muted("2. Create or join a clan for buffs and allies."),
                        ThemePalette.muted("3. Bind abilities via /class bind <ability> <slot>."),
                        ThemePalette.accent("Pro tip: toggle the HUD via /calanity hud toggle."))));

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

    private ItemStack clanSummary(Player player) {
        return clanManager.findByMember(player.getUniqueId())
                .map(clan -> ItemStacks.themed(
                        Material.SHIELD,
                        ThemePalette.subtitle(clan.displayName()),
                        List.of(
                                ThemePalette.accent("Power: " + clan.power()),
                                ThemePalette.muted("Members: " + clan.members().size()),
                                ThemePalette.muted("Captains: " + clan.members().stream()
                                        .filter(member -> member.role().name().equalsIgnoreCase("CAPTAIN"))
                                        .count()),
                                ThemePalette.accent("Click for leadership tools."))))
                .orElseGet(() -> ItemStacks.themed(
                        Material.PAPER,
                        ThemePalette.subtitle("No Clan Yet"),
                        List.of(
                                ThemePalette.muted("Use /clan create <name> to found your banner."),
                                ThemePalette.muted("Or /clan join <name> to ally up."),
                                ThemePalette.accent("Click to view clan actions."))));
    }

    private List<Component> leaderboardLore() {
        List<Component> lore = new ArrayList<>();
        clanManager.leaderboard(3).forEach(entry ->
                lore.add(ThemePalette.accent(entry.displayName() + " » " + entry.power())));
        if (lore.isEmpty()) {
            lore.add(ThemePalette.muted("No clans have earned power yet."));
        } else {
            lore.add(ThemePalette.muted("Click to view the full leaderboard."));
        }
        return lore;
    }
}
