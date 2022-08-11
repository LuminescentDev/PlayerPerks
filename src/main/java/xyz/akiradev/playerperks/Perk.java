package xyz.akiradev.playerperks;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import xyz.akiradev.playerperks.managers.LocaleManager;
import xyz.akiradev.playerperks.managers.PerkManager;

import java.util.Objects;

public abstract class Perk {

    private final Material defaultMaterial;
    private final String name;
    private final String description;
    private final String ID;
    private final int defaultCost;

    public Perk(Material defaultMaterial, String name, String description, String ID, int defaultCost) {
        this.defaultMaterial = defaultMaterial;
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.defaultCost = defaultCost;
    }

    public GuiItem createGUIItem(Boolean enabled) {
        LocaleManager localeManager = PlayerPerks.getInstance().getManager(LocaleManager.class);
        String cost;

        if(getCost() < 0){
            cost = localeManager.getLocaleMessage("points-you-get", StringPlaceholders.single("points", getCost() * -1));
        }else{
            cost = "Cost: " + getCost();
        }

        return ItemBuilder.from(getMaterial())
                .name(Component.text(enabled ? ChatColor.GREEN + getName() : ChatColor.RED + getName()))
                .lore(Component.text(getDescription()), Component.text(""),
                        Component.text(""),
                        Component.text(cost))
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

    public abstract void onPurchase(HumanEntity player);
    public abstract void onSell(HumanEntity player);

}
