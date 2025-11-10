package dev.sergeantfuzzy.calanity.ui.gui;

import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/** Handles clicks within Calanity menus. */
public final class MenuListener implements Listener {

    private final ClanMainMenu clanMainMenu;
    private final ClanMembersMenu membersMenu;
    private final ClanSettingsMenu settingsMenu;
    private final ClanLeaderboardMenu leaderboardMenu;
    private final ClassSelectMenu classSelectMenu;
    private final ClassManager classManager;
    private final ProfileService profileService;
    private final HudManager hudManager;

    public MenuListener(ClanMainMenu clanMainMenu,
                        ClanMembersMenu membersMenu,
                        ClanSettingsMenu settingsMenu,
                        ClanLeaderboardMenu leaderboardMenu,
                        ClassSelectMenu classSelectMenu,
                        ClassManager classManager,
                        ProfileService profileService,
                        HudManager hudManager) {
        this.clanMainMenu = clanMainMenu;
        this.membersMenu = membersMenu;
        this.settingsMenu = settingsMenu;
        this.leaderboardMenu = leaderboardMenu;
        this.classSelectMenu = classSelectMenu;
        this.classManager = classManager;
        this.profileService = profileService;
        this.hudManager = hudManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        holder.action(event.getSlot()).ifPresent(action -> handleAction(player, holder.type(), action));
    }

    private void handleAction(Player player, MenuType type, String action) {
        if ("CLOSE".equalsIgnoreCase(action)) {
            player.closeInventory();
            return;
        }
        if (action.startsWith("BACK:")) {
            openTarget(player, action.substring("BACK:".length()));
            return;
        }
        switch (action) {
            case "OPEN_MEMBERS" -> player.openInventory(membersMenu.build(player));
            case "OPEN_SETTINGS" -> player.openInventory(settingsMenu.build(player));
            case "OPEN_LEADERBOARD" -> player.openInventory(leaderboardMenu.build());
            case "REFRESH_LEADERBOARD" -> player.openInventory(leaderboardMenu.build());
            case "OPEN_CLASS_MENU" -> classSelectMenu.open(player);
            case "SHOW_COMMANDS" -> sendCommandGuide(player);
            default -> {
                if (action.startsWith("SELECT_CLASS:")) {
                    selectClass(player, action.substring("SELECT_CLASS:".length()));
                }
            }
        }
    }

    private void openTarget(Player player, String target) {
        switch (target) {
            case "CLAN_MAIN" -> player.openInventory(clanMainMenu.build(player));
            case "CLAN_MEMBERS" -> player.openInventory(membersMenu.build(player));
            case "CLAN_SETTINGS" -> player.openInventory(settingsMenu.build(player));
            default -> player.closeInventory();
        }
    }

    private void selectClass(Player player, String classId) {
        classManager.findById(classId).ifPresentOrElse(clazz -> {
            classManager.assign(player.getUniqueId(), clazz);
            profileService.setClass(player.getUniqueId(), clazz.id());
            profileService.save(player.getUniqueId());
            hudManager.refresh(player);
            Messenger.success(player, "Equipped the " + clazz.displayName() + " class.");
        }, () -> Messenger.error(player, "Class not found. Please report this to an admin."));
    }

    private void sendCommandGuide(Player player) {
        Messenger.list(player, "Clan & Utility Commands", List.of(
                ThemePalette.accent("/clan create <name>").append(Component.text(" — start a new banner", ThemePalette.NEUTRAL)),
                ThemePalette.accent("/clan invite <player>").append(Component.text(" — recruit allies", ThemePalette.NEUTRAL)),
                ThemePalette.accent("/clan promote <player>").append(Component.text(" — elevate to captain", ThemePalette.NEUTRAL)),
                ThemePalette.accent("/calanity reload").append(Component.text(" — hot-reload configs safely", ThemePalette.NEUTRAL)),
                ThemePalette.accent("/calanity hud toggle").append(Component.text(" — switch the HUD on/off", ThemePalette.NEUTRAL)),
                ThemePalette.accent("/class bind <ability> <slot>").append(Component.text(" — update hotbar bindings", ThemePalette.NEUTRAL))
        ));
    }
}
