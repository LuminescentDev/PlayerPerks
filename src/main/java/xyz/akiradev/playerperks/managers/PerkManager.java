package xyz.akiradev.playerperks.managers;

import com.google.common.collect.ObjectArrays;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.RoseGardenUtils;
import xyz.akiradev.playerperks.Perk;
import xyz.akiradev.playerperks.PlayerPerks;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerkManager extends Manager {

    private CommentedFileConfiguration config;
    private final Map<String, Perk> perks = new HashMap<>();

    public PerkManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    private void setDefaultSettings(Perk perk){
        File directory = new File(PlayerPerks.getInstance().getDataFolder(), "perks");
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        File file = new File(directory, perk.getID() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(file);

        boolean changed = this.setIfNotExists("name", perk.getDefaultName());
        changed |= this.setIfNotExists("description", perk.getDefaultDescription());
        changed |= this.setIfNotExists("material", perk.getDefaultMaterial().name());
        changed |= this.setIfNotExists("cost", perk.getDefaultCost());
        changed |= this.setIfNotExists("CustomModelID", perk.getDefaultCustomModelID());
        changed |= this.setIfNotExists("blacklisted-perks", perk.getDefaultBlacklistedPerks());
        changed |= this.setIfNotExists("enabled", true);

        if (changed) this.config.save();

    }

    private boolean setIfNotExists(String setting, Object value, String... comments) {
        if (this.config.get(setting) != null)
            return false;

        String defaultMessage = "Default: ";
        if (value instanceof String && RoseGardenUtils.containsConfigSpecialCharacters((String) value)) {
            defaultMessage += "'" + value + "'";
        } else {
            defaultMessage += value;
        }

        this.config.set(setting, value, ObjectArrays.concat(comments, new String[] { defaultMessage }, String.class));
        return true;
    }

    public CommentedFileConfiguration loadSettings(Perk perk){
        File directory = new File(PlayerPerks.getInstance().getDataFolder(), "perks");
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        File file = new File(directory, perk.getID() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(file);
        return this.config;
    }

    public void registerPerk(Perk perk) {
        setDefaultSettings(perk);
        if(!this.loadSettings(perk).getBoolean("enabled")) return;
        perks.put(perk.getID(), perk);
        PlayerPerks.getInstance().getLogger().info("Registering Perk: " + perk.getName());
    }

    public Collection<Perk> getPerks() {
        return this.perks.values();
    }

    public Perk getPerk(String ID) {
        return this.perks.get(ID);
    }

    public boolean isBlacklisted(String perk, String perkToCheck) {
        List<String> blacklistedPerks = this.loadSettings(getPerk(perk)).getStringList("blacklisted-perks");
        return blacklistedPerks.size() > 0 && blacklistedPerks.contains(perkToCheck);
    }

    @Override
    public void reload() {
        for (Perk perk : PlayerPerks.getInstance().getManager(PerkManager.class).getPerks()) {
            loadSettings(perk);
        }
    }

    @Override
    public void disable() {

    }

}
