package dev.sergeantfuzzy.calanity.storage.sql;

/** MySQL implementation using HikariCP. */
public final class MysqlDriver extends SqlStore {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MysqlDriver(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    protected String jdbcUrl() {
        return "jdbc:mysql://" + host + ':' + port + '/' + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    @Override
    protected String driverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String username() {
        return username;
    }

    @Override
    protected String password() {
        return password;
    }
}
