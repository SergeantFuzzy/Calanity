package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.profile.ProfileService;
import dev.sergeantfuzzy.calanity.gameplay.hud.HudManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClassChangeSub implements ClassSub {

    private final ClassManager classManager;
    private final ProfileService profileService;
    private final HudManager hudManager;

    public ClassChangeSub(ClassManager classManager, ProfileService profileService, HudManager hudManager) {
        this.classManager = classManager;
        this.profileService = profileService;
        this.hudManager = hudManager;
    }

    @Override
    public String name() {
        return "change";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 2) {
            Messenger.usage(sender, "/class change <class>");
            return true;
        }
        classManager.findById(args[1]).ifPresentOrElse(
                clazz -> {
                    classManager.assign(player.getUniqueId(), clazz);
                    profileService.setClass(player.getUniqueId(), clazz.id());
                    profileService.save(player.getUniqueId());
                    Messenger.success(sender, "Switched to " + clazz.displayName() + ".");
                    hudManager.refresh(player);
                },
                () -> Messenger.error(sender, "Class not found."));
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
