package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.TabSuggestions;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class InfoSub implements ClanSub {

    private final ClanManager clanManager;

    public InfoSub(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Messenger.usage(sender, "/clan info <name>");
            return true;
        }
        clanManager.findById(args[1]).ifPresentOrElse(
                clan -> Messenger.list(sender, "Clan Overview", List.of(
                        ThemePalette.subtitle("Name: ").append(ThemePalette.muted(clan.displayName())),
                        ThemePalette.subtitle("Power: ").append(ThemePalette.muted(String.valueOf(clan.power()))),
                        ThemePalette.subtitle("Members: ").append(ThemePalette.muted(String.valueOf(clan.members().size()))))),
                () -> Messenger.warn(sender, "Clan not found."));
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
