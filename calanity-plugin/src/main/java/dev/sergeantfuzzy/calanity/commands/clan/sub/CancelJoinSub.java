package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CancelJoinSub implements ClanSub {

    private final ClanFacade clanFacade;

    public CancelJoinSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "canceljoin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            clanFacade.denyInvite(player, null);
            Messenger.info(sender, "Cleared every pending clan invite.");
        }
        return true;
    }
}
