package dev.sergeantfuzzy.calanity.api.events;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.classes.AbilityContext;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/** Fired when an ability is about to be executed. */
public class AbilityTriggerEvent extends CalanityEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Ability ability;
    private final AbilityContext context;
    private final String cause;
    private boolean cancelled;

    public AbilityTriggerEvent(CalanityAPI api, Ability ability, AbilityContext context, String cause) {
        super(api, false);
        this.ability = ability;
        this.context = context;
        this.cause = cause;
    }

    public Ability ability() {
        return ability;
    }

    public AbilityContext context() {
        return context;
    }

    public String cause() {
        return cause;
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
