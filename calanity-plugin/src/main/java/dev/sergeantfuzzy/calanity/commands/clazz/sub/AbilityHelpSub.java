package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class AbilityHelpSub implements ClassSub {

    private final AbilityManager abilityManager;

    public AbilityHelpSub(AbilityManager abilityManager) {
        this.abilityManager = abilityManager;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/class help <ability>");
            return true;
        }
        abilityManager.find(args[1]).ifPresentOrElse(
                ability -> Messenger.list(sender, ability.displayName(), List.of(
                        ThemePalette.subtitle("Type: ").append(ThemePalette.muted(ability.type().name())),
                        ThemePalette.subtitle("Cooldown: ").append(ThemePalette.muted(ability.cooldown().toSeconds() + "s")),
                        ThemePalette.muted("Bind: /class bind " + ability.id() + " <slot>"))),
                () -> Messenger.error(sender, "Ability not found."));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.abilityIds(abilityManager, args[1]);
        }
        return List.of();
    }
}
