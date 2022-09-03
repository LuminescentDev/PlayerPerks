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

    public void addPerk(String uuid, String perk) {
        this.async(() -> this.getDatabaseConnector().connect(connection -> {
            boolean hasPerk;
            String selectQuery = "SELECT * FROM " + this.getTablePrefix() + "perks WHERE uuid = ? AND perk = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, uuid);
                statement.setString(2, perk);
                ResultSet resultSet = statement.executeQuery();
                hasPerk = resultSet.next();
            }
            if(!hasPerk) {
                String insertQuery = "INSERT INTO " + this.getTablePrefix() + "perks (uuid, perk) " +
                        "VALUES (?, ?)";
                try (PreparedStatement pstatement = connection.prepareStatement(insertQuery)) {
                    pstatement.setString(1, uuid);
                    pstatement.setString(2, perk);
                    pstatement.executeUpdate();
                }
            }
        }));
    }

    public void removePerk(String uuid, String perk) {
        this.async(() -> this.getDatabaseConnector().connect(connection -> {
            String deleteQuery = "DELETE FROM " + this.getTablePrefix() + "perks WHERE uuid = ? AND perk = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setString(1, uuid);
                statement.setString(2, perk);
                statement.executeUpdate();
            }
        }));
    }

    public void clearPerks(String uuid){
        this.async(() -> this.getDatabaseConnector().connect(connection -> {
            String deleteQuery = "DELETE FROM " + this.getTablePrefix() + "perks WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setString(1, uuid);
                statement.executeUpdate();
            }
        }));
    }

    public void getPlayerData(UUID uuid, Consumer<PlayerData> callback) {
        if(this.playerData.containsKey(uuid)) {
            callback.accept(this.playerData.get(uuid));
            return;
        }

        this.async(() -> this.databaseConnector.connect(connection -> {
            PlayerData playerData;
            String dataQuery = "SELECT * FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(dataQuery)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()){
                    int points = resultSet.getInt("points");
                    long resetCooldown = resultSet.getLong("reset_cooldown");

                    playerData = new PlayerData(uuid);
                    playerData.setPoints(points);
                    playerData.setCooldown(resetCooldown);

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
                    playerData.addPerk(perk);
                }
            }
        }));
    }

    public void updatePlayerData(PlayerData playerData){
        this.async(() -> this.databaseConnector.connect(connection -> {
            boolean create;

            String checkQuery = "SELECT 1 FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
                statement.setString(1, playerData.getUUID().toString());
                ResultSet result = statement.executeQuery();
                create = !result.next();
            }

            if(create){
                String insertQuery = "INSERT INTO " + this.getTablePrefix() + "player_data (uuid, points, reset_cooldown) " +
                        "VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, playerData.getUUID().toString());
                    statement.setInt(2, playerData.getPoints());
                    statement.setLong(3, playerData.getCooldown());
                    statement.executeUpdate();
                }
            }else{
                String updateQuery = "UPDATE " + this.getTablePrefix() + "player_data SET points = ?, reset_cooldown = ? WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, playerData.getPoints());
                    statement.setString(2, playerData.getUUID().toString());
                    statement.setLong(3, playerData.getCooldown());
                    statement.executeUpdate();
                }
            }
        }));
    }

    public void savePlayerData(){
        this.databaseConnector.connect(connection -> {
            for(PlayerData playerData : this.playerData.values()){
                this.updatePlayerData(playerData);
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
