package xyz.akiradev.playerperks;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Perk {

    private Material defaultMaterial;
    private String name;
    private String description;
    private String ID;
    private int defaultCost;

    public Perk(Material defaultMaterial, String name, String description, String ID, int defaultCost) {
        this.defaultMaterial = defaultMaterial;
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.defaultCost = defaultCost;
    }

    public GuiItem createGUIItem() {
        return ItemBuilder.from(defaultMaterial)
                .name(Component.text(name))
                .lore(Component.text(description), Component.text(""),
                        Component.text(""),
                        Component.text("Cost: " + defaultCost))
                .asGuiItem();

    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
        return ID;
    }

    public int getDefaultCost() {
        return defaultCost;
    }

    public abstract void onPurchase(HumanEntity player);

}
