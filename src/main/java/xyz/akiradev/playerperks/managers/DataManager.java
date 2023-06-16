package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.MySQLConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import xyz.akiradev.playerperks.PlayerData;
import xyz.akiradev.playerperks.database.migrations._1_Create_Tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class DataManager extends AbstractDataManager implements Listener {

    private final Map<UUID, PlayerData> playerData;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.playerData = new HashMap<>();
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Collections.singletonList(_1_Create_Tables.class);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.computeIfAbsent(uuid, PlayerData::new);
    }

    public void unloadPlayerData(UUID uuid) {
        this.playerData.remove(uuid);
    }

    public void addPerk(String uuid, String perk) {
        this.async(() -> this.getDatabaseConnector().connect(connection -> {
            String insertQuery;
            if (this.isMySQLConnector()) {
                insertQuery = "INSERT INTO " + this.getTablePrefix() + "perks (uuid, perk) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE uuid = uuid";
            } else {
                insertQuery = "INSERT OR IGNORE INTO " + this.getTablePrefix() + "perks (uuid, perk) VALUES (?, ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, uuid);
                statement.setString(2, perk);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }


    public void removePerk(String uuid, String perk) {
        this.async(() -> this.getDatabaseConnector().connect(connection -> {
            String deleteQuery;
            deleteQuery = "DELETE FROM " + this.getTablePrefix() + "perks WHERE uuid = ? AND perk = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setString(1, uuid);
                statement.setString(2, perk);
                statement.executeUpdate();
            }
        }));
    }

    public void getPlayerData(UUID uuid, Consumer<PlayerData> callback) {
        PlayerData playerData = this.playerData.get(uuid);
        if (playerData != null) {
            callback.accept(playerData);
            return;
        }

        final PlayerData finalPlayerData = new PlayerData(uuid);  // Declare as final

        this.async(() -> this.databaseConnector.connect(connection -> {
            String query = "SELECT * FROM " + this.getTablePrefix() + "player_data pd " + "LEFT JOIN " + this.getTablePrefix() + "perks pk ON pd.uuid = pk.uuid " + "WHERE pd.uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int points = resultSet.getInt("points");
                    String perk = resultSet.getString("perk");

                    if (perk != null) {
                        finalPlayerData.addPerk(perk);
                    }
                    finalPlayerData.setPoints(points);
                }

                this.playerData.put(uuid, finalPlayerData);
                callback.accept(finalPlayerData);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }


    public void updatePlayerData(PlayerData playerData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String upsertQuery;
            if (this.isMySQLConnector()) {
                upsertQuery = "INSERT INTO " + this.getTablePrefix() + "player_data (uuid, points, reset_cooldown) " + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE points = VALUES(points), reset_cooldown = VALUES(reset_cooldown)";
            } else {
                upsertQuery = "INSERT OR REPLACE INTO " + this.getTablePrefix() + "player_data (uuid, points, reset_cooldown) " + "VALUES (?, ?, ?)";
            }
            try (PreparedStatement statement = connection.prepareStatement(upsertQuery)) {
                statement.setString(1, playerData.getUUID().toString());
                statement.setInt(2, playerData.getPoints());
                statement.setLong(3, playerData.getCooldown());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    private boolean isMySQLConnector() {
        return this.getDatabaseConnector() instanceof MySQLConnector;
    }

    public void savePlayerData() {
        this.databaseConnector.connect(connection -> {
            String upsertQuery = "INSERT INTO " + this.getTablePrefix() + "player_data (uuid, points, reset_cooldown) " + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE points = VALUES(points), reset_cooldown = VALUES(reset_cooldown)";

            try (PreparedStatement statement = connection.prepareStatement(upsertQuery)) {
                for (PlayerData playerData : this.playerData.values()) {
                    statement.setString(1, playerData.getUUID().toString());
                    statement.setInt(2, playerData.getPoints());
                    statement.setLong(3, playerData.getCooldown());
                    statement.addBatch();
                }

                statement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    private void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, asyncCallback);
    }

    private void sync(Runnable syncCallback) {
        Bukkit.getScheduler().runTask(this.rosePlugin, syncCallback);
    }
}
