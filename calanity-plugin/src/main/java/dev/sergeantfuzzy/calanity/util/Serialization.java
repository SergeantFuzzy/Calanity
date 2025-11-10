package dev.sergeantfuzzy.calanity.util;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/** Tiny serialization helpers for YAML/JSON configs. */
public final class Serialization {

    private Serialization() {
    }

    public static Map<String, Object> flatten(ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        for (String key : section.getKeys(false)) {
            map.put(key, section.get(key));
        }
        return map;
    }
}
