package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DisbandSub implements ClanSub {

    private final ClanFacade clanFacade;

    public DisbandSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "disband";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            boolean disbanded = clanFacade.disband(player);
            if (disbanded) {
                Messenger.warn(sender, "Clan disbanded. All members have been released.");
            } else {
                Messenger.error(sender, "Only the clan leader can disband the clan.");
            }
        }
        return true;
    }
}
