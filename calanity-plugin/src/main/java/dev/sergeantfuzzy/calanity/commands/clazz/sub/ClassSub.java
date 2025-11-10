package dev.sergeantfuzzy.calanity.commands.clazz.sub;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ClassSub {
    String name();

    boolean execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
