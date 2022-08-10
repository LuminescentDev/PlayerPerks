package xyz.akiradev.playerperks;

import xyz.akiradev.playerperks.managers.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private int points;
    private List<String> perks;

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.points = 0;
        this.perks = new ArrayList<>();
    }

    public void save(){
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<String> getPerks() {
        return this.perks;
    }

    public void addPerk(UUID uuid, String perk){
        this.perks.add(perk);
    }

    public void removePerk(UUID uuid, String perk){
        this.perks.remove(perk);
    }

}
