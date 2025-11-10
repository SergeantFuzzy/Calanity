package dev.sergeantfuzzy.calanity.commands.clan;

import dev.sergeantfuzzy.calanity.commands.clan.sub.*;
import dev.sergeantfuzzy.calanity.domain.clans.ClanFacade;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.ui.gui.ClanMainMenu;
import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Root /clan command. */
public final class ClanCommand implements CommandExecutor, TabCompleter {

    private final Map<String, ClanSub> subs = new HashMap<>();
    private final ClanMainMenu menu;

    public ClanCommand(ClanManager clanManager, ClanFacade clanFacade, ClanMainMenu menu) {
        this.menu = menu;
        register(new CreateSub(clanFacade));
        register(new JoinSub(clanFacade, clanManager));
        register(new LeaveSub(clanFacade));
        register(new ListSub(clanManager));
        register(new InfoSub(clanManager));
        register(new InviteSub(clanFacade));
        register(new AcceptSub(clanFacade));
        register(new DenySub(clanFacade));
        register(new CancelJoinSub(clanFacade));
        register(new PromoteSub(clanFacade));
        register(new DisbandSub(clanFacade));
    }

    private void register(ClanSub sub) {
        subs.put(sub.name(), sub);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                menu.open(player);
            } else {
                Messenger.warn(sender, "Console must specify a subcommand.");
            }
            return true;
        }
        ClanSub sub = subs.get(args[0].toLowerCase(Locale.ROOT));
        if (sub == null) {
            Messenger.warn(sender, "Unknown clan subcommand.");
            return true;
        }
        return sub.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subs.keySet().stream().filter(it -> it.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        }
        ClanSub sub = subs.get(args[0].toLowerCase(Locale.ROOT));
        return sub == null ? List.of() : sub.tabComplete(sender, args);
    }
}
