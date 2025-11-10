package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import dev.sergeantfuzzy.calanity.api.stats.StatKey;
import dev.sergeantfuzzy.calanity.domain.classes.ClassLoreLibrary;
import dev.sergeantfuzzy.calanity.domain.classes.ClassLoreLibrary.ClassLore;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.util.ItemStacks;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Lists all registered classes for selection. */
public final class ClassSelectMenu {

    private final ClassManager classManager;

    public ClassSelectMenu(ClassManager classManager) {
        this.classManager = classManager;
    }

    public Inventory build() {
        MenuHolder holder = new MenuHolder(MenuType.CLASS_SELECT);
        Inventory inv = Bukkit.createInventory(holder, 54, ThemePalette.title("Select Your Path"));
        holder.inventory(inv);
        fill(inv);

        inv.setItem(4, ItemStacks.themed(
                Material.ENCHANTED_BOOK,
                ThemePalette.subtitle("How Class Selection Works"),
                List.of(
                        ThemePalette.muted("• Click a class to equip it instantly."),
                        ThemePalette.muted("• Bind abilities via /class bind <ability> <slot>."),
                        ThemePalette.muted("• Toggle HUD with /calanity hud toggle."),
                        ThemePalette.accent("Add-ons can register more classes automatically."))));

        int slot = 10;
        for (PlayerClass clazz : classManager.all()) {
            inv.setItem(slot, classItem(clazz));
            holder.action(slot, "SELECT_CLASS:" + clazz.id());
            slot = nextSlot(slot);
        }

        inv.setItem(45, ItemStacks.backButton("Gameplay"));
        holder.action(45, "CLOSE");
        inv.setItem(49, ItemStacks.closeButton());
        holder.action(49, "CLOSE");
        return inv;
    }

    public void open(Player player) {
        player.openInventory(build());
    }

    private void fill(Inventory inv) {
        ItemStack filler = ItemStacks.fillerPane();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler.clone());
        }
    }

    private ItemStack classItem(PlayerClass clazz) {
        ClassLore lore = ClassLoreLibrary.describe(clazz.id());
        List<Component> lines = new ArrayList<>();
        lines.add(ThemePalette.accent("Role: " + lore.role()));
        lines.add(ThemePalette.muted(lore.description()));
        lines.add(Component.text(""));
        lines.add(ThemePalette.subtitle("Base Stats").decorate(TextDecoration.BOLD));
        lines.add(ThemePalette.muted("❤ Health: " + (int) clazz.baseStats().get(StatKey.HEALTH)));
        lines.add(ThemePalette.muted("🛡 Defense: " + (int) clazz.baseStats().get(StatKey.DEFENSE)));
        lines.add(ThemePalette.muted("⚔ Damage: " + (int) clazz.baseStats().get(StatKey.DAMAGE)));
        lines.add(Component.text(""));
        addAbilitySection("Active Abilities", clazz.actives(), lines);
        addAbilitySection("Passive Traits", clazz.passives(), lines);
        addAbilitySection("Clan Ability", clazz.clanAbilities(), lines);
        lines.add(Component.text(""));
        lore.tips().forEach(tip -> lines.add(ThemePalette.accent("Tip: " + tip)));

        Material icon = switch (clazz.id().toLowerCase()) {
            case "elfin" -> Material.GLOW_BERRIES;
            case "panzer" -> Material.NETHERITE_CHESTPLATE;
            case "bargainer" -> Material.GOLDEN_SWORD;
            case "craftist" -> Material.CRAFTING_TABLE;
            case "elementalist" -> Material.HEART_OF_THE_SEA;
            default -> Material.NETHER_STAR;
        };
        return ItemStacks.themed(icon, ThemePalette.subtitle(clazz.displayName()), lines);
    }

    private void addAbilitySection(String title, Collection<? extends Ability> abilities, List<Component> lines) {
        lines.add(ThemePalette.subtitle(title));
        if (abilities.isEmpty()) {
            lines.add(ThemePalette.muted("• None"));
            return;
        }
        abilities.forEach(ability -> lines.add(ThemePalette.muted("• " + ability.displayName())));
    }

    private int nextSlot(int slot) {
        slot++;
        if ((slot + 1) % 9 == 0) {
            slot += 2;
        }
        return slot;
    }
}
