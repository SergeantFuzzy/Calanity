package dev.sergeantfuzzy.calanity.api.placeholders;

import java.util.Optional;
import java.util.UUID;

/** Provides values for %calanity_*% placeholders. */
public interface PlaceholderSource {

    Optional<String> resolve(UUID playerId, String placeholderId);
}
