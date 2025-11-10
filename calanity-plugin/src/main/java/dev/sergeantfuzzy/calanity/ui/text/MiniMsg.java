package dev.sergeantfuzzy.calanity.ui.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/** Shared MiniMessage instance. */
public final class MiniMsg {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private MiniMsg() {
    }

    public static Component parse(String input) {
        return MINI.deserialize(input);
    }
}
