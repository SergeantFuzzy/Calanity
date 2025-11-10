package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class PromoteSub implements ClanSub {

    private final ClanFacade clanFacade;

    public PromoteSub(ClanFacade clanFacade) {
        this.clanFacade = clanFacade;
    }

    @Override
    public String name() {
        return "promote";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 2) {
            Messenger.usage(sender, "/clan promote <player>");
            return true;
        }
        var target = Bukkit.getOfflinePlayer(args[1]);
        boolean promoted = clanFacade.promote(player, target);
        if (promoted) {
            Messenger.success(sender, "Promotion successful.");
        } else {
            Messenger.error(sender, "Unable to promote that player (check roles).");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player player && args.length == 2) {
            return clanFacade.clanMemberSuggestions(player.getUniqueId(), args[1]);
        }
        return List.of();
    }
}
