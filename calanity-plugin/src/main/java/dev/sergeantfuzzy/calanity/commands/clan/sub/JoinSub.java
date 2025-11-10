package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class JoinSub implements ClanSub {

    private final ClanFacade clanFacade;
    private final ClanManager clanManager;

    public JoinSub(ClanFacade clanFacade, ClanManager clanManager) {
        this.clanFacade = clanFacade;
        this.clanManager = clanManager;
    }

    @Override
    public String name() {
        return "join";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 2) {
            Messenger.usage(sender, "/clan join <name>");
            return true;
        }
        boolean joined = clanFacade.join(player, args[1]);
        if (joined) {
            Messenger.success(sender, "Welcome to the clan! Use /clan to open the hub.");
        } else {
            Messenger.error(sender, "Unable to join that clan (maybe full or invite-only).");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.clanNames(clanManager, args[1]);
        }
        return List.of();
    }
}
