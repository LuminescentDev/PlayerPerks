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
import java.util.Map;

public class PerkManager extends Manager {

    private CommentedFileConfiguration config;

    private static PerkManager instance;
    private final Map<String, Perk> perks = new HashMap<>();
    private final Map<String, Perk> perIDs = new HashMap<>();

    public PerkManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        instance = this;
    }

    public static PerkManager getInstance() {
        return instance;
    }

    private void setDefaultSettings(Perk perk){
        File directory = new File(PlayerPerks.getInstance().getDataFolder(), "perks");
        directory.mkdirs();

        File file = new File(directory, perk.getID() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(file);

        boolean changed = this.setIfNotExists("name", perk.getName());
        changed |= this.setIfNotExists("description", perk.getDescription());
        changed |= this.setIfNotExists("material", perk.getDefaultMaterial().name());
        changed |= this.setIfNotExists("cost", perk.getDefaultCost());

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
        directory.mkdirs();

        File file = new File(directory, perk.getID() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(file);
        return this.config;
    }

    public void registerPerk(Perk perk) {
        instance.perks.put(perk.getName(), perk);
        instance.perIDs.put(perk.getID(), perk);
        PlayerPerks.getInstance().getLogger().info("Registering Perk: " + perk.getName());
        setDefaultSettings(perk);
    }

    public Collection<Perk> getPerks() {
        return this.perks.values();
    }

    public Perk getPerk(String name) {
        return this.perks.get(name);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

}
