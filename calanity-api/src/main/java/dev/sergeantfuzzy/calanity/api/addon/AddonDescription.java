package dev.sergeantfuzzy.calanity.api.addon;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Lightweight addon metadata similar to plugin.yml but specific to Calanity modules.
 */
public final class AddonDescription {

    private final String id;
    private final String version;
    private final Collection<String> authors;

    public AddonDescription(String id, String version, Collection<String> authors) {
        this.id = Objects.requireNonNull(id, "id");
        this.version = Objects.requireNonNull(version, "version");
        this.authors = List.copyOf(authors == null ? List.of("unknown") : authors);
    }

    public String id() {
        return id;
    }

    public String version() {
        return version;
    }

    public Collection<String> authors() {
        return authors;
    }
}
