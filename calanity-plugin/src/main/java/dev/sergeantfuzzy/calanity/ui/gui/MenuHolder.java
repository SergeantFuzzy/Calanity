package dev.sergeantfuzzy.calanity.ui.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MenuHolder implements InventoryHolder {

    private final MenuType type;
    private final Map<Integer, String> actions = new HashMap<>();
    private Inventory inventory;

    public MenuHolder(MenuType type) {
        this.type = type;
    }

    public MenuType type() {
        return type;
    }

    public void inventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void action(int slot, String action) {
        actions.put(slot, action);
    }

    public Optional<String> action(int slot) {
        return Optional.ofNullable(actions.get(slot));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
