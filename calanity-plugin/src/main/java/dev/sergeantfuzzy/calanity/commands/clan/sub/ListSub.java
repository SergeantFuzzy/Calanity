package dev.sergeantfuzzy.calanity.commands.clan.sub;

import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.ThemePalette;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public final class ListSub implements ClanSub {

    private final ClanManager clanManager;

    public ListSub(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (clanManager.all().isEmpty()) {
            Messenger.warn(sender, "No clans have been created yet.");
            return true;
        }
        List<Component> lines = clanManager.all().stream()
                .map(clan -> ThemePalette.accent(clan.displayName())
                        .append(ThemePalette.muted(" (" + clan.members().size() + " members)")))
                .collect(Collectors.toList());
        Messenger.list(sender, "Active Clans", lines);
        return true;
    }
}
