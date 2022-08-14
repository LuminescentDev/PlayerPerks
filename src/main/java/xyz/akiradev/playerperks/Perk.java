package xyz.akiradev.playerperks;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import xyz.akiradev.playerperks.managers.LocaleManager;
import xyz.akiradev.playerperks.managers.PerkManager;

import java.util.List;
import java.util.Objects;

public abstract class Perk {

    private final Material defaultMaterial;
    private final String name;
    private final String description;
    private final String ID;
    private final int defaultCost;
    private final int defaultCustomModelID;
    private final List<String> defaultBlacklistedPerks;

    public Perk(Material defaultMaterial, String name, String description, String ID, int cost, int customModelID) {
        this.defaultMaterial = defaultMaterial;
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.defaultCost = cost;
        this.defaultBlacklistedPerks = List.of();
        this.defaultCustomModelID = customModelID;
    }

    public Perk(Material defaultMaterial, String name, String description, String ID, int cost, int customModelID, List<String> defaultBlacklistedPerks) {
        this.defaultMaterial = defaultMaterial;
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.defaultCost = cost;
        this.defaultBlacklistedPerks = defaultBlacklistedPerks;
        this.defaultCustomModelID = customModelID;
    }

    public GuiItem createGUIItem(Boolean enabled) {
        LocaleManager localeManager = PlayerPerks.getInstance().getManager(LocaleManager.class);
        String cost;
        StringBuilder blacklistedPerks = new StringBuilder();
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if(getCost() < 0){
            cost = localeManager.getLocaleMessage("points-you-get", StringPlaceholders.single("points", getCost() * -1));
        }else{
            cost = "Cost: " + getCost();
        }
        for (String blacklistedPerk : getBlacklistedPerks()) {
            if(perkManager.getPerkByID(blacklistedPerk) != null){
                blacklistedPerks.append(perkManager.getPerkByID(blacklistedPerk).getName()).append(", ");
            }
        }
        return ItemBuilder.from(getMaterial())
                .name(Component.text(enabled ? ChatColor.GREEN + getName() : ChatColor.RED + getName()))
                .lore(Component.text(getDescription()),
                        Component.text(""),
                        Component.text(cost),
                        Component.text(""),
                        Component.text("Blacklisted perks: " + blacklistedPerks))
                .model(getCustomModelID())
                .glow(enabled)
                .asGuiItem();
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public String getDefaultName() {
        return name;
    }

    public String getDefaultDescription() {
        return description;
    }

    public String getID() {
        return ID;
    }

    public int getDefaultCost() {
        return defaultCost;
    }

    public List<String> getDefaultBlacklistedPerks() {
        return defaultBlacklistedPerks;
    }

    public int getdefaultCustomModelID() {
        return defaultCustomModelID;
    }

    public Material getMaterial(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if(perkManager.loadSettings(this).getString("material") == null){
            return defaultMaterial;
        }
        return Material.getMaterial(Objects.requireNonNull(perkManager.loadSettings(this).getString("material")));
    }

    public String getName(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if(perkManager.loadSettings(this).getString("name") == null){
            return name;
        }
        return perkManager.loadSettings(this).getString("name");
    }

    public String getDescription(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if(perkManager.loadSettings(this).getString("description") == null){
            return description;
        }
        return perkManager.loadSettings(this).getString("description");
    }

    public int getCost(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        if(perkManager.loadSettings(this).getInt("cost") == 0){
            return defaultCost;
        }
        return perkManager.loadSettings(this).getInt("cost");
    }

    public List<String> getBlacklistedPerks(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        return perkManager.loadSettings(this).getStringList("blacklisted-perks");
    }

    public int getCustomModelID(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        return perkManager.loadSettings(this).getInt("custom-model-id");
    }

    public boolean getEnabled(){
        PerkManager perkManager = PlayerPerks.getInstance().getManager(PerkManager.class);
        return perkManager.loadSettings(this).getBoolean("enabled");
    }

    public abstract void onPurchase(HumanEntity player);
    public abstract void onSell(HumanEntity player);

}
