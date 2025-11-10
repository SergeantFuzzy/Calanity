package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Handles /clan create <name>. */
public final class CreateSub implements ClanSub {

    private final ClanFacade clanFacade;

    public CreateSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "create";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messenger.warn(sender, "Only players can create clans.");
            return true;
        }
        if (args.length < 2) {
            Messenger.usage(sender, "/clan create <name>");
            return true;
        }
        boolean created = clanFacade.createClan(player, args[1]).isPresent();
        if (created) {
            Messenger.success(sender, "Clan created! Invite allies with /clan invite <player>.");
        } else {
            Messenger.error(sender, "Unable to create clan (name may be taken or you are already in one).");
        }
        return true;
    }
}
