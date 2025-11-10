package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ClassMembersListSub implements ClassSub {

    private final ClassManager classManager;

    public ClassMembersListSub(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/class list <name>");
            return true;
        }
        String targetId = args[1].toLowerCase(Locale.ROOT);
        classManager.findById(targetId).ifPresentOrElse(clazz -> {
            List<Component> members = classManager.assignments().entrySet().stream()
                    .filter(entry -> entry.getValue().id().equalsIgnoreCase(targetId))
                    .map(entry -> {
                        String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                        if (name == null) {
                            name = entry.getKey().toString().substring(0, 8);
                        }
                        return ThemePalette.accent(name);
                    })
                    .collect(Collectors.toList());
            if (members.isEmpty()) {
                Messenger.info(sender, "No players currently use " + clazz.displayName() + ".");
            } else {
                Messenger.list(sender, clazz.displayName() + " Players", members);
            }
        }, () -> Messenger.error(sender, "Class not found."));
        return true;
    }
}
