package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.ItemStacks;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Leadership + management guidance menu. */
public final class ClanSettingsMenu {

    private final ClanManager clanManager;

    public ClanSettingsMenu(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public Inventory build(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.CLAN_SETTINGS);
        Inventory inv = Bukkit.createInventory(holder, 36, ThemePalette.title("Clan Management"));
        holder.inventory(inv);
        fill(inv);

        Clan clan = clanManager.findByMember(player.getUniqueId()).orElse(null);
        if (clan == null) {
            inv.setItem(4, ItemStacks.themed(
                    Material.PAPER,
                    ThemePalette.subtitle("Create Your Clan"),
                    List.of(
                            ThemePalette.muted("You need a clan to unlock these options."),
                            ThemePalette.accent("Use /clan create <name>"),
                            ThemePalette.accent("or accept an invite via /clan accept."))));
        } else {
            inv.setItem(4, ItemStacks.themed(
                    Material.COPPER_BLOCK,
                    ThemePalette.subtitle(clan.displayName()),
                    List.of(
                            ThemePalette.accent("Power: " + clan.power()),
                            ThemePalette.muted("Members: " + clan.members().size()),
                            ThemePalette.muted("Leaders: " + clan.members().stream()
                                    .filter(member -> member.role().name().equalsIgnoreCase("LEADER"))
                                    .count()))));
        }

        inv.setItem(10, ItemStacks.themed(
                Material.AMETHYST_SHARD,
                ThemePalette.subtitle("Leadership Tools"),
                List.of(
                        ThemePalette.muted("• /clan promote <player>"),
                        ThemePalette.muted("• /clan disband"),
                        ThemePalette.accent("Leaders can delegate captains for invites."))));

        inv.setItem(12, ItemStacks.themed(
                Material.WRITABLE_BOOK,
                ThemePalette.subtitle("Recruitment & Invites"),
                List.of(
                        ThemePalette.muted("• /clan invite <player>"),
                        ThemePalette.muted("• /clan accept | /clan deny"),
                        ThemePalette.accent("Invites expire after 5 minutes—follow up quickly."))));

        inv.setItem(14, ItemStacks.themed(
                Material.GLOWSTONE_DUST,
                ThemePalette.subtitle("Clan Buffs & Synergy"),
                List.of(
                        ThemePalette.muted("Stack class abilities to trigger clan passives."),
                        ThemePalette.accent("Customize buffs inside config.yml -> clans section."),
                        ThemePalette.muted("Future add-ons can add more synergy options."))));

        inv.setItem(16, ItemStacks.themed(
                Material.TNT,
                ThemePalette.subtitle("Danger Zone"),
                List.of(
                        ThemePalette.muted("• /clan leave"),
                        ThemePalette.muted("• /clan disband"),
                        ThemePalette.danger("These actions are irreversible!"))));

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
}
