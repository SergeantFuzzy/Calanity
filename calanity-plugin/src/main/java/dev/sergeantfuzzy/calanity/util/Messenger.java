package dev.sergeantfuzzy.calanity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

/** Consistent themed messaging helpers. */
public final class Messenger {

    private static final Component PREFIX = Component.text()
            .append(Component.text("Calanity", ThemePalette.PRIMARY).decorate(TextDecoration.BOLD))
            .append(Component.text(" │ ", ThemePalette.NEUTRAL))
            .build();

    private Messenger() {
    }

    public static void info(CommandSender sender, String message) {
        send(sender, ThemePalette.NEUTRAL, message);
    }

    public static void success(CommandSender sender, String message) {
        send(sender, ThemePalette.HIGHLIGHT, message);
    }

    public static void warn(CommandSender sender, String message) {
        send(sender, ThemePalette.SECONDARY, message);
    }

    public static void error(CommandSender sender, String message) {
        send(sender, ThemePalette.NEGATIVE, message);
    }

    public static void debug(CommandSender sender, String message) {
        send(sender, ThemePalette.SECONDARY, "[Debug] " + message);
    }

    public static void usage(CommandSender sender, String usage) {
        send(sender, ThemePalette.HIGHLIGHT, "Usage: " + usage);
    }

    public static void list(CommandSender sender, String header, List<Component> entries) {
        Objects.requireNonNull(entries, "entries");
        sender.sendMessage(line(Component.text(header, ThemePalette.PRIMARY).decorate(TextDecoration.BOLD)));
        entries.forEach(entry -> sender.sendMessage(line(Component.text("• ", ThemePalette.SECONDARY).append(entry))));
    }

    public static void line(CommandSender sender, Component body) {
        sender.sendMessage(line(body));
    }

    private static void send(CommandSender sender, TextColor color, String message) {
        sender.sendMessage(line(Component.text(message, color)));
    }

    private static Component line(Component body) {
        return PREFIX.append(body);
    }
}
