package xyz.akiradev.playerperks.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_Create_Tables extends DataMigration {

    public _1_Create_Tables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE " + tablePrefix + "player_data (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "points INTEGER NOT NULL, " +
                    "points_total INTEGER NOT NULL, " +
                    "reset_cooldown BIGINT NOT NULL, " +
                    "UNIQUE (uuid)" +
                    ")");
            statement.executeUpdate("CREATE TABLE " + tablePrefix + "perks (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "perk VARCHAR(36) NOT NULL" +
                    ")");
        }
    }
}
