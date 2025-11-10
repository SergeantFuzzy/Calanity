package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.api.classes.Passive;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class PassiveListSub implements ClassSub {

    private final ClassManager classManager;

    public PassiveListSub(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String name() {
        return "passive";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/class passive <class>");
            return true;
        }
        String classId = args[1].toLowerCase(Locale.ROOT);
        classManager.findById(classId).ifPresentOrElse(clazz -> {
            if (clazz.passives().isEmpty()) {
                Messenger.info(sender, clazz.displayName() + " has no passive abilities.");
                return;
            }
            List<Component> lines = clazz.passives().stream()
                    .map(Passive::displayName)
                    .map(name -> ThemePalette.accent("• " + name))
                    .collect(Collectors.toList());
            Messenger.list(sender, clazz.displayName() + " Passives", lines);
        }, () -> Messenger.error(sender, "Class not found."));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.classIds(classManager, args[1]);
        }
        return List.of();
    }
}
