package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class InviteSub implements ClanSub {

    private final ClanFacade clanFacade;

    public InviteSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "invite";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 2) {
            Messenger.usage(sender, "/clan invite <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messenger.warn(sender, "Player not found.");
            return true;
        }
        boolean invited = clanFacade.invite(player, target);
        if (invited) {
            Messenger.success(sender, "Invite sent to " + target.getName() + ".");
        } else {
            Messenger.error(sender, "Unable to invite that player (check role or clan size).");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return TabSuggestions.onlinePlayers(args[1]);
        }
        return List.of();
    }
}
