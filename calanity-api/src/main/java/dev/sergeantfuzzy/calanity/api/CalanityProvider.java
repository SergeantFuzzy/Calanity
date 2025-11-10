package dev.sergeantfuzzy.calanity.api;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple holder that lets the runtime register/unregister the currently active API.
 */
public final class CalanityProvider {

    private static final AtomicReference<CalanityAPI> HANDLE = new AtomicReference<>();

    private CalanityProvider() {
    }

    public static void register(CalanityAPI api) {
        Objects.requireNonNull(api, "api");
        if (!HANDLE.compareAndSet(null, api)) {
            throw new IllegalStateException("CalanityAPI already registered");
        }
    }

    public static void unregister(CalanityAPI api) {
        HANDLE.compareAndSet(api, null);
    }

    public static CalanityAPI require() {
        CalanityAPI api = HANDLE.get();
        if (api == null) {
            throw new IllegalStateException("CalanityAPI not available (plugin disabled?)");
        }
        return api;
    }
}
