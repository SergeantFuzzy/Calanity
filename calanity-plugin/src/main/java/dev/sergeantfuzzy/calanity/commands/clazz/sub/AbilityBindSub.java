package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.binding.AbilityBindingService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class AbilityBindSub implements ClassSub {

    private final AbilityManager abilityManager;
    private final AbilityBindingService bindingService;
    private final HudManager hudManager;

    public AbilityBindSub(AbilityManager abilityManager,
                          AbilityBindingService bindingService,
                          HudManager hudManager) {
        this.abilityManager = abilityManager;
        this.bindingService = bindingService;
        this.hudManager = hudManager;
    }

    @Override
    public String name() {
        return "bind";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 3) {
            Messenger.usage(sender, "/class bind <ability> <slot>");
            return true;
        }
        int slot;
        try {
            slot = Integer.parseInt(args[2]) - 1;
        } catch (NumberFormatException ex) {
            Messenger.warn(sender, "Slot must be a number between 1-9.");
            return true;
        }
        if (slot < 0 || slot > 8) {
            Messenger.warn(sender, "Slot must be within your hotbar (1-9).");
            return true;
        }
        abilityManager.find(args[1]).ifPresentOrElse(
                ability -> {
                    bindingService.bind(player.getUniqueId(), slot, ability.id());
                    Messenger.success(sender, "Bound " + ability.displayName() + " to slot " + (slot + 1) + ".");
                    hudManager.refresh(player);
                },
                () -> Messenger.error(sender, "Ability not found. Check /class help <ability>."));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.abilityIds(abilityManager, args[1]);
        }
        if (args.length == 3) {
            return TabSuggestions.integers(1, 9, args[2]);
        }
        return List.of();
    }
}
