package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import xyz.akiradev.playerperks.PlayerData;
import xyz.akiradev.playerperks.database.migrations._1_Create_Tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        return Collections.singletonList(
                _1_Create_Tables.class
        );
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void unloadPlayerData(UUID uuid) {
        this.playerData.remove(uuid);
    }

    public void getPerks(UUID uuid) {

    }

    public void addPerk(String uuid, String perk) {
        this.async(() -> {
            this.getDatabaseConnector().connect(connection -> {
                String insertQuery = "INSERT INTO " + this.getTablePrefix() + "perks (uuid, perk) " +
                        "VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, perk);
                    statement.executeUpdate();
                }
            });
        });
    }

    public void removePerk(String uuid, String perk) {
        this.async(() -> {
            this.getDatabaseConnector().connect(connection -> {
                String deleteQuery = "DELETE FROM " + this.getTablePrefix() + "perks WHERE uuid = ? AND perk = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, perk);
                    statement.executeUpdate();
                }
            });
        });
    }

    public void getPlayerData(UUID uuid, Consumer<PlayerData> callback) {
        if(this.playerData.containsKey(uuid)) {
            callback.accept(this.playerData.get(uuid));
            return;
        }

        this.async(() -> {
            this.databaseConnector.connect(connection -> {
                PlayerData playerData;
                String dataQuery = "SELECT * FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(dataQuery)) {
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()){
                        int points = resultSet.getInt("points");

                        playerData = new PlayerData(uuid);
                        playerData.setPoints(points);

                        this.playerData.put(uuid, playerData);
                    }else{
                        playerData = new PlayerData(uuid);
                        this.playerData.put(uuid, playerData);
                        callback.accept(playerData);
                    }
                }

                String perksQuery = "SELECT * FROM " + this.getTablePrefix() + "perks WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(perksQuery)) {
                    statement.setString(1, uuid.toString());
                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next()){
                        String perk = resultSet.getString("perk");
                        playerData.addPerk(uuid, perk);
                    }
                }
            });
        });
    }

    public void updatePlayerData(PlayerData playerData){
        this.async(() -> {
            this.databaseConnector.connect(connection -> {
                boolean create;

                String checkQuery = "SELECT 1 FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
                    statement.setString(1, playerData.getUuid().toString());
                    ResultSet result = statement.executeQuery();
                    create = !result.next();
                }

                if(create){
                    String insertQuery = "INSERT INTO " + this.getTablePrefix() + "player_data (uuid, points) " +
                            "VALUES (?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                        statement.setString(1, playerData.getUuid().toString());
                        statement.setInt(2, playerData.getPoints());
                        statement.executeUpdate();
                    }
                }else{
                    String updateQuery = "UPDATE " + this.getTablePrefix() + "player_data SET points = ? WHERE uuid = ?";
                    try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                        statement.setInt(1, playerData.getPoints());
                        statement.setString(2, playerData.getUuid().toString());
                        statement.executeUpdate();
                    }
                }
            });
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
