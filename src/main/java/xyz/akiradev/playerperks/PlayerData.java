package xyz.akiradev.playerperks;

import org.bukkit.Bukkit;
import xyz.akiradev.playerperks.managers.ConfigManager;
import xyz.akiradev.playerperks.managers.DataManager;
import xyz.akiradev.playerperks.managers.PerkManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int points;
    private final List<String> perks;

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.points = ConfigManager.Setting.BASE_POINTS.getInt();
        this.perks = new ArrayList<>();
    }

    public void save(){
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = Math.min(points, ConfigManager.Setting.MAX_POINTS.getInt());
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
    }

    public boolean addPoints(int points) {
        if(this.points + points > ConfigManager.Setting.MAX_POINTS.getInt()){
            return false;
        }
        this.points += points;
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
        return true;
    }

    public boolean removePoints(int points) {
        if (this.points - points < 0) {
            return false;
        }
        this.points -= points;
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
        return true;
    }

    public List<String> getPerks() {
        return perks;
    }

    public boolean addPerk(String perkID){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if (perks.contains(perkID) || ConfigManager.Setting.MAX_PERKS.getInt() <= this.perks.size()) {
            return false;
        }
        for(String perkToCheck : perks){
            if(perkManager.isBlacklisted(perkID, perkToCheck)){
                return false;
            }
        }
        perks.add(perkID);
        return true;
    }

    public boolean removePerk(String perk){
        if (!perks.contains(perk)) {
            return false;
        }
        perks.remove(perk);
        PlayerPerks.getInstance().getManager(DataManager.class).removePerk(String.valueOf(uuid), perk);
        return true;
    }

    public void resetPerks(){
        Iterator<String> iterator = perks.iterator();
        while(iterator.hasNext()){
            String perkID = iterator.next();
            iterator.remove();
            Perk perk = PlayerPerks.getInstance().getManager(PerkManager.class).getPerkByID(perkID);
            perk.onSell(Bukkit.getPlayer(uuid));
            addPoints(perk.getCost());
        }
        PlayerPerks.getInstance().getManager(DataManager.class).updatePlayerData(this);
    }

    public boolean hasPerk(String perk){
        return this.perks.contains(perk);
    }

}
