package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import xyz.akiradev.playerperks.PlayerData;
import xyz.akiradev.playerperks.PlayerPerks;

public class GUIManager extends Manager {

    private static PaginatedGui gui;

    public GUIManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void createGUI(){
        gui = Gui.paginated()
                .title(Component.text("Perks!"))
                .rows(6)
                .create();
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text("Previous")).asGuiItem(event -> gui.previous()));
        gui.setItem(6, 5, ItemBuilder.from(Material.EMERALD).name(Component.text("Perk Points: 0")).asGuiItem());
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text("Previous")).asGuiItem(event -> gui.next()));
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setOpenGuiAction(action -> {
             PlayerData playerData = PlayerPerks.getInstance().getManager(DataManager.class).getPlayerData(action.getPlayer().getUniqueId());
            gui.updateItem(6, 5, ItemBuilder.from(Material.EMERALD).name(Component.text("Perk Points: " + playerData.getPoints())).asGuiItem());
            PlayerPerks.getInstance().getManager(PerkManager.class).getPerks().forEach(perk -> {
                GuiItem guiItem = perk.createGUIItem();
                guiItem.setAction(event -> {
                    perk.onPurchase(event.getWhoClicked());
                });
                gui.addItem(guiItem);
            });
        });
    }

    public PaginatedGui getGUI(){
        return gui;
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
