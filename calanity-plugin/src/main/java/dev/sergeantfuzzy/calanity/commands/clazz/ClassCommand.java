package dev.sergeantfuzzy.calanity.commands.clazz;

import dev.sergeantfuzzy.calanity.commands.clazz.sub.*;
import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.classes.binding.AbilityBindingService;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.ui.gui.ClassSelectMenu;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class ClassCommand implements CommandExecutor, TabCompleter {

    private final Map<String, ClassSub> subs = new HashMap<>();
    private final ClassSelectMenu menu;

    public ClassCommand(ClassManager classManager,
                        AbilityManager abilityManager,
                        AbilityBindingService bindingService,
                        ProfileService profileService,
                        ClassSelectMenu menu,
                        HudManager hudManager) {
        this.menu = menu;
        register(new ClassInfoSub(classManager));
        register(new ClassChangeSub(classManager, profileService, hudManager));
        register(new ClassMembersListSub(classManager));
        register(new AbilityBindSub(abilityManager, bindingService, hudManager));
        register(new AbilityHelpSub(abilityManager));
        register(new PassiveListSub(classManager));
        register(new PassiveInfoSub(classManager));
    }

    private void register(ClassSub sub) {
        subs.put(sub.name(), sub);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                menu.open(player);
            }
            return true;
        }
        ClassSub sub = subs.get(args[0].toLowerCase(Locale.ROOT));
        if (sub == null) {
            Messenger.warn(sender, "Unknown class subcommand.");
            return true;
        }
        return sub.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subs.keySet().stream().filter(it -> it.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        }
        ClassSub sub = subs.get(args[0].toLowerCase(Locale.ROOT));
        return sub == null ? List.of() : sub.tabComplete(sender, args);
    }
}
