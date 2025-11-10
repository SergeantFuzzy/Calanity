package dev.sergeantfuzzy.calanity.gameplay.hud;

import dev.sergeantfuzzy.calanity.util.Tasker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Controls HUD toggles per player. */
public final class HudManager {

    private final SidebarRenderer renderer;
    private final Tasker tasker;
    private final Map<UUID, Boolean> toggles = new ConcurrentHashMap<>();

    public HudManager(SidebarRenderer renderer, Tasker tasker) {
        this.renderer = renderer;
        this.tasker = tasker;
    }

    public void initialize(Player player, boolean enabled) {
        toggles.put(player.getUniqueId(), enabled);
        apply(player, enabled);
    }

    public boolean toggle(Player player) {
        boolean enabled = !toggles.getOrDefault(player.getUniqueId(), true);
        set(player, enabled);
        return enabled;
    }

    public void set(Player player, boolean enabled) {
        toggles.put(player.getUniqueId(), enabled);
        apply(player, enabled);
    }

    public void refresh(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            refresh(player);
        }
    }

    public void refreshClan(dev.sergeantfuzzy.calanity.api.clans.Clan clan) {
        clan.members().forEach(member -> refresh(member.uuid()));
    }

    public void refresh(Player player) {
        if (toggles.getOrDefault(player.getUniqueId(), true)) {
            runScoreboardTask(player, () -> renderer.render(player));
        }
    }

    public void remove(UUID playerId) {
        toggles.remove(playerId);
    }

    private void apply(Player player, boolean enabled) {
        runScoreboardTask(player, () -> {
            if (enabled) {
                renderer.render(player);
            } else {
                var manager = Bukkit.getScoreboardManager();
                if (manager != null) {
                    player.setScoreboard(manager.getMainScoreboard());
                }
            }
        });
    }

    private void runScoreboardTask(Player player, Runnable action) {
        Runnable safeAction = () -> {
            if (!player.isOnline()) {
                return;
            }
            action.run();
        };
        if (Bukkit.isPrimaryThread()) {
            safeAction.run();
        } else {
            tasker.runSync(safeAction);
        }
    }
}
