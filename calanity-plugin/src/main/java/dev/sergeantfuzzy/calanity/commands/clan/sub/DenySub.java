package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class DenySub implements ClanSub {

    private final ClanFacade clanFacade;

    public DenySub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "deny";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        String clanName = args.length >= 2 ? args[1] : null;
        boolean denied = clanFacade.denyInvite(player, clanName);
        if (denied) {
            Messenger.info(sender, "Invite cleared.");
        } else {
            Messenger.warn(sender, "No pending invite to deny.");
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
