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
import java.util.Optional;

public final class PassiveInfoSub implements ClassSub {

    private final ClassManager classManager;

    public PassiveInfoSub(ClassManager classManager) {
        this.classManager = classManager;
    }

    @Override
    public String name() {
        return "passiveinfo";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/class passiveinfo <passiveId>");
            return true;
        }
        String search = args[1].toLowerCase(Locale.ROOT);
        Optional<PassiveMatch> match = classManager.all().stream()
                .flatMap(clazz -> clazz.passives().stream().map(passive -> new PassiveMatch(clazz.displayName(), passive)))
                .filter(matchEntry -> matchEntry.passive().id().equalsIgnoreCase(search)
                        || matchEntry.passive().displayName().equalsIgnoreCase(search))
                .findFirst();
        match.ifPresentOrElse(found -> {
            Passive passive = found.passive();
            List<Component> lines = List.of(
                    ThemePalette.subtitle("Class: ").append(ThemePalette.muted(found.className())),
                    ThemePalette.subtitle("Passive: ").append(ThemePalette.muted(passive.displayName())),
                    ThemePalette.subtitle("Cooldown: ").append(ThemePalette.muted(passive.cooldown().toSeconds() + "s"))
            );
            Messenger.list(sender, "Passive Overview", lines);
        }, () -> Messenger.error(sender, "Passive not found."));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.passiveIds(classManager, args[1]);
        }
        return List.of();
    }

    private record PassiveMatch(String className, Passive passive) {
    }
}
