package dev.sergeantfuzzy.calanity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/** Centralized Calanity brand palette + helpers. */
public final class ThemePalette {

    public static final TextColor PRIMARY = TextColor.color(0xD9, 0x77, 0x06);   // #d97706
    public static final TextColor SECONDARY = TextColor.color(0xB4, 0x53, 0x09); // #b45309
    public static final TextColor HIGHLIGHT = TextColor.color(0xFB, 0xBF, 0x24); // #fbbf24
    public static final TextColor NEGATIVE = TextColor.color(0xEF, 0x44, 0x44);  // #ef4444
    public static final TextColor NEUTRAL = TextColor.color(0xD1, 0xD5, 0xDB);   // #d1d5db
    public static final TextColor FRAME = TextColor.color(0x1F, 0x1F, 0x1F);     // #1f1f1f

    private ThemePalette() {
    }

    public static Component title(String text) {
        return Component.text(text, PRIMARY).decorate(TextDecoration.BOLD);
    }

    public static Component subtitle(String text) {
        return Component.text(text, SECONDARY).decorate(TextDecoration.BOLD);
    }

    public static Component accent(String text) {
        return Component.text(text, HIGHLIGHT);
    }

    public static Component muted(String text) {
        return Component.text(text, NEUTRAL);
    }

    public static Component danger(String text) {
        return Component.text(text, NEGATIVE);
    }
}
