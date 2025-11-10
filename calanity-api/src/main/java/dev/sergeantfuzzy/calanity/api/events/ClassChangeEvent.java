package dev.sergeantfuzzy.calanity.api.events;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import dev.sergeantfuzzy.calanity.api.classes.PlayerClass;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/** Fired before a player's class changes (can be cancelled). */
public class ClassChangeEvent extends CalanityEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerId;
    private final PlayerClass previousClass;
    private final PlayerClass nextClass;
    private boolean cancelled;

    public ClassChangeEvent(CalanityAPI api, UUID playerId, PlayerClass previousClass, PlayerClass nextClass) {
        super(api, false);
        this.playerId = playerId;
        this.previousClass = previousClass;
        this.nextClass = nextClass;
    }

    public UUID playerId() {
        return playerId;
    }

    public PlayerClass previousClass() {
        return previousClass;
    }

    public PlayerClass nextClass() {
        return nextClass;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
