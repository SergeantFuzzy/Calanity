package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class AcceptSub implements ClanSub {

    private final ClanFacade clanFacade;

    public AcceptSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "accept";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        String clanName = args.length >= 2 ? args[1] : null;
        boolean accepted = clanFacade.acceptInvite(player, clanName);
        if (accepted) {
            Messenger.success(sender, "Invite accepted. Welcome to the clan!");
        } else {
            Messenger.warn(sender, "No pending invite found for that clan.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player player && args.length == 2) {
            return clanFacade.pendingInvites(player.getUniqueId(), args[1]);
        }
        return List.of();
    }
}
