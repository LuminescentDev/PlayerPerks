package xyz.akiradev.playerperks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.akiradev.playerperks.managers.ConfigManager;
import xyz.akiradev.playerperks.managers.DataManager;
import xyz.akiradev.playerperks.managers.PerkManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final List<String> perks;
    private int points;
    private Long cooldown;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.cooldown = 0L;
        this.points = ConfigManager.Setting.BASE_POINTS.getInt();
        this.perks = new ArrayList<>();
    }

    public void save() {
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
    }

    public Long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean addPoints(int points) {
        if (this.points + points > ConfigManager.Setting.MAX_POINTS.getInt()) {
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

    public boolean addPerk(String perkID) {
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if (perks.contains(perkID) || ConfigManager.Setting.MAX_PERKS.getInt() <= this.perks.size()) {
            return false;
        }
        for (String perkToCheck : perks) {
            if (perkManager.isBlacklisted(perkID, perkToCheck)) {
                return false;
            }
        }
        perks.add(perkID);
        return true;
    }

    public void removePerk(Perk perk) {
        String id = perk.getID();
        if (!perks.contains(id)) {
            return;
        }
        perk.onSell(Bukkit.getPlayer(uuid));
        perks.remove(id);
        PlayerPerks.getInstance().getManager(DataManager.class).removePerk(String.valueOf(uuid), perk.getID());
    }

    public void resetPerks() {
        DataManager dataManager = PlayerPerks.getInstance().getManager(DataManager.class);
        int totalCost = 0;
        List<String> perksToRemove = new ArrayList<>();

        for (String perkID : perks) {
            Perk perk = PlayerPerks.getInstance().getManager(PerkManager.class).getPerk(perkID);
            totalCost += perk.getCost();
            perk.onSell(Bukkit.getPlayer(uuid));
            perksToRemove.add(perkID);
        }

        for (String perkID : perksToRemove) {
            perks.remove(perkID);
            PlayerPerks.getInstance().getManager(DataManager.class).removePerk(String.valueOf(uuid), perkID);
        }

        // Update player data
        PlayerData playerData = dataManager.getPlayerData(this.uuid);
        playerData.addPoints(totalCost);
        playerData.setCooldown(Utils.createCooldown(
                ConfigManager.Setting.PERK_RESET_COOLDOWN_DAYS.getInt(),
                ConfigManager.Setting.PERK_RESET_COOLDOWN_HOURS.getInt(),
                ConfigManager.Setting.PERK_RESET_COOLDOWN_MINUTES.getInt(),
                ConfigManager.Setting.PERK_RESET_COOLDOWN_SECONDS.getInt()
        ));
        dataManager.updatePlayerData(playerData);
    }


    public boolean hasPerk(String perk) {
        Player player = Bukkit.getPlayer(uuid);
        return this.perks.contains(perk) && player != null && player.hasPermission("playerperks.perk." + perk);
    }

}
