-- baseline tables (player_data, clan_data)
CREATE TABLE IF NOT EXISTS player_data (
    uuid CHAR(36) PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    class_id VARCHAR(32),
    clan_id VARCHAR(32),
    stats_json TEXT,
    hud_enabled BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS clan_data (
    id VARCHAR(32) PRIMARY KEY,
    display_name VARCHAR(64) NOT NULL,
    power INT NOT NULL,
    members_json TEXT NOT NULL
);
