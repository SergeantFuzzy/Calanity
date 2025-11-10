package dev.sergeantfuzzy.calanity.debug;

import dev.sergeantfuzzy.calanity.util.Messenger;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Lightweight debug logger toggled via /calanity debug. */
public final class DebugLogger {

    private final Logger logger;
    private volatile boolean enabled;

    public DebugLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
        logger.log(Level.INFO, () -> "Debug mode " + (enable ? "enabled" : "disabled"));
    }

    public void tell(CommandSender sender, String message) {
        if (enabled) {
            Messenger.debug(sender, message);
        }
    }

    public void debug(String message) {
        if (enabled) {
            logger.log(Level.INFO, "[Debug] {0}", message);
        }
    }
}
