package dev.sergeantfuzzy.calanity.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/** Quick sound helper. */
public final class Sounds {

    private Sounds() {
    }

    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.1f);
    }

    public static void playError(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 0.7f);
    }
}
