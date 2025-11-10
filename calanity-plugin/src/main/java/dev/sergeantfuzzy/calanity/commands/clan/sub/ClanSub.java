package dev.sergeantfuzzy.calanity.commands.clan.sub;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ClanSub {
    String name();

    boolean execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
