package dev.sergeantfuzzy.calanity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

/** Helper for building themed GUI items. */
public final class ItemStacks {

    private ItemStacks() {
    }

    public static ItemStack themed(Material material, Component name, List<Component> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(name);
        if (lore != null) {
            meta.lore(lore);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack fillerPane() {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(Component.text(" "));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        return pane;
    }

    public static ItemStack closeButton() {
        return themed(
                Material.BARRIER,
                ThemePalette.danger("Close Menu").decorate(TextDecoration.BOLD),
                List.of(ThemePalette.muted("Click to exit this interface.")));
    }

    public static ItemStack backButton(String destination) {
        return themed(
                Material.ARROW,
                ThemePalette.subtitle("Back"),
                List.of(
                        ThemePalette.muted("Return to " + destination),
                        ThemePalette.muted("Click to navigate back.")));
    }

    public static ItemStack playerHead(UUID uuid, String displayName, List<Component> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        meta.setOwningPlayer(offline);
        meta.displayName(ThemePalette.accent(displayName).decorate(TextDecoration.BOLD));
        if (lore != null) {
            meta.lore(lore);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        head.setItemMeta(meta);
        return head;
    }
}
