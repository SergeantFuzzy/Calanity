package dev.sergeantfuzzy.calanity.api.events;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import dev.sergeantfuzzy.calanity.api.clans.Clan;
import org.bukkit.event.HandlerList;

/** Fired when a clan's power changes (kills, admin edits, etc.). */
public class ClanPowerChangeEvent extends CalanityEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Clan clan;
    private final int previous;
    private final int updated;

    public ClanPowerChangeEvent(CalanityAPI api, Clan clan, int previous, int updated) {
        super(api, false);
        this.clan = clan;
        this.previous = previous;
        this.updated = updated;
    }

    public Clan clan() {
        return clan;
    }

    public int previous() {
        return previous;
    }

    public int updated() {
        return updated;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
