package dev.sergeantfuzzy.calanity.storage;

/** Supported storage providers. */
public enum StorageType {
    YAML,
    SQLITE,
    MYSQL;

    public static StorageType from(String raw) {
        try {
            return StorageType.valueOf(raw.toUpperCase());
        } catch (Exception ex) {
            return YAML;
        }
    }
}
