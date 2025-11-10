package dev.sergeantfuzzy.calanity.storage.sql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.domain.profile.PlayerProfile;
import dev.sergeantfuzzy.calanity.storage.DataStore;
import dev.sergeantfuzzy.calanity.storage.util.ClanCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/** JDBC-backed DataStore (SQLite/MySQL). */
public abstract class SqlStore implements DataStore {

    private static final Gson GSON = new Gson();
    private HikariDataSource dataSource;
    private ExecutorService executor;

    protected abstract String jdbcUrl();

    protected abstract String driverClass();

    protected abstract String username();

    protected abstract String password();

    @Override
    public void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl());
        if (username() != null) {
            config.setUsername(username());
        }
        if (password() != null) {
            config.setPassword(password());
        }
        config.setDriverClassName(driverClass());
        config.setMaximumPoolSize(5);
        config.setPoolName("CalanitySQL");
        this.dataSource = new HikariDataSource(config);
        this.executor = Executors.newFixedThreadPool(4, new SqlThreadFactory());
        runMigrations();
    }

    @Override
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public CompletableFuture<Void> savePlayer(PlayerProfile profile) {
        return CompletableFuture.runAsync(() -> {
            String sql = "REPLACE INTO player_data(uuid,name,class_id,clan_id,stats_json,hud_enabled) VALUES (?,?,?,?,?,?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, profile.uuid().toString());
                statement.setString(2, profile.name());
                statement.setString(3, profile.classId());
                statement.setString(4, profile.clanId());
                Map<String, Double> stats = new HashMap<>();
                profile.stats().asMap().forEach((key, value) -> stats.put(key.name(), value));
                statement.setString(5, GSON.toJson(stats));
                statement.setBoolean(6, profile.hudEnabled());
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<PlayerProfile> loadPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT name,class_id,clan_id,stats_json,hud_enabled FROM player_data WHERE uuid = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet rs = statement.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", rs.getString("name"));
                    map.put("class", rs.getString("class_id"));
                    map.put("clan", rs.getString("clan_id"));
                    map.put("hudEnabled", rs.getBoolean("hud_enabled"));
                    String statsJson = rs.getString("stats_json");
                    if (statsJson != null) {
                        Map<String, Double> stats = GSON.fromJson(statsJson, new TypeToken<Map<String, Double>>() {
                        }.getType());
                        map.put("stats", stats);
                    }
                    return PlayerProfile.deserialize(uuid, map);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Map<String, Clan>> loadClans() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Clan> clans = new HashMap<>();
            String sql = "SELECT id, display_name, power, members_json FROM clan_data";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("displayName", rs.getString("display_name"));
                    map.put("power", rs.getInt("power"));
                    List<Map<String, Object>> members = GSON.fromJson(rs.getString("members_json"),
                            new TypeToken<List<Map<String, Object>>>() {
                            }.getType());
                    map.put("members", members);
                    Clan clan = ClanCodec.fromMap(map);
                    clans.put(clan.id(), clan);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return clans;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> saveClan(Clan clan) {
        return CompletableFuture.runAsync(() -> {
            String sql = "REPLACE INTO clan_data(id, display_name, power, members_json) VALUES (?,?,?,?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, clan.id());
                statement.setString(2, clan.displayName());
                statement.setInt(3, clan.power());
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> members = (List<Map<String, Object>>) ClanCodec.toMap(clan).get("members");
                statement.setString(4, GSON.toJson(members));
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteClan(String clanId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM clan_data WHERE id = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, clanId);
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }

    private void runMigrations() {
        for (String resource : List.of("storage/sql/migrations/V1__init.sql", "storage/sql/migrations/V2__indices.sql")) {
            try (Connection connection = dataSource.getConnection()) {
                executeSqlScript(connection, resource);
            } catch (SQLException | IOException ex) {
                throw new RuntimeException("Failed to run migrations", ex);
            }
        }
    }

    private void executeSqlScript(Connection connection, String resource) throws IOException, SQLException {
        try (InputStream stream = SqlStore.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException("Missing migration: " + resource);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
                for (String statementSql : builder.toString().split(";")) {
                    String trimmed = statementSql.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                try (Statement statement = connection.createStatement()) {
                    statement.execute(trimmed);
                }
            }
        }
    }
    }

    private static final class SqlThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "calanity-sql-" + counter++);
            thread.setDaemon(true);
            return thread;
        }
    }
}
