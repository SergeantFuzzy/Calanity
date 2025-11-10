package dev.sergeantfuzzy.calanity.api.events;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import org.bukkit.event.Event;

/** Base Bukkit event type for all Calanity events. */
public abstract class CalanityEvent extends Event {

    private final CalanityAPI api;

    protected CalanityEvent(CalanityAPI api, boolean async) {
        super(async);
        this.api = api;
    }

    public CalanityAPI api() {
        return api;
    }
}
