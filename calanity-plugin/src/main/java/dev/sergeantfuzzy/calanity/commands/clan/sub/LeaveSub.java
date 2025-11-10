package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LeaveSub implements ClanSub {

    private final ClanFacade clanFacade;

    public LeaveSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "leave";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            boolean left = clanFacade.leaveClan(player);
            if (left) {
                Messenger.info(sender, "You left your clan.");
            } else {
                Messenger.warn(sender, "You are not currently in a clan.");
            }
        }
        return true;
    }
}
