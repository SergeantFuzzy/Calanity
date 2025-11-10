package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public final class ClassInfoSub implements ClassSub {

    private final ClassManager classManager;

    public ClassInfoSub(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/class info <name>");
            return true;
        }
        classManager.findById(args[1]).ifPresentOrElse(clazz -> {
            String clanAbilities = clazz.clanAbilities().isEmpty()
                    ? "None"
                    : clazz.clanAbilities().stream().map(Ability::displayName).collect(Collectors.joining(", "));
            List<Component> details = List.of(
                    ThemePalette.subtitle("Display: ").append(ThemePalette.muted(clazz.displayName())),
                    ThemePalette.subtitle("Actives: ").append(ThemePalette.muted(clazz.actives().size() + "")),
                    ThemePalette.subtitle("Passives: ").append(ThemePalette.muted(clazz.passives().size() + "")),
                    ThemePalette.subtitle("Clan Abilities: ").append(ThemePalette.muted(clanAbilities))
            );
            Messenger.list(sender, "Class Details", details);
        }, () -> Messenger.error(sender, "Class not found."));
        return true;
    }
}
