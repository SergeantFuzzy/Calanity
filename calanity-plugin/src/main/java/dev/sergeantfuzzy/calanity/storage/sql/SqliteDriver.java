package dev.sergeantfuzzy.calanity.storage.sql;

import java.nio.file.Path;

/** SQLite implementation backed by a single .db file. */
public final class SqliteDriver extends SqlStore {

    private final Path file;

    public SqliteDriver(Path dataFolder) {
        this.file = dataFolder.resolve("calanity.db");
    }

    @Override
    protected String jdbcUrl() {
        return "jdbc:sqlite:" + file.toAbsolutePath();
    }

    @Override
    protected String driverClass() {
        return "org.sqlite.JDBC";
    }

    @Override
    protected String username() {
        return null;
    }

    @Override
    protected String password() {
        return null;
    }
}
