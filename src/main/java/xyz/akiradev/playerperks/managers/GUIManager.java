package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.akiradev.playerperks.Perk;
import xyz.akiradev.playerperks.PlayerData;
import xyz.akiradev.playerperks.PlayerPerks;
import xyz.akiradev.playerperks.Utils;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class GUIManager extends Manager {

    private static PaginatedGui gui;

    public GUIManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void createGUI(Player viewer) {
        PlayerData playerData = PlayerPerks.getInstance().getManager(DataManager.class).getPlayerData(viewer.getUniqueId());
        LocaleManager localeManager = PlayerPerks.getInstance().getManager(LocaleManager.class);
        gui = Gui.paginated()
                .title(Component.text("Perks!"))
                .rows(6)
                .create();
        Collection<Perk> perks = PlayerPerks.getInstance().getManager(PerkManager.class).getPerks();
        List<Perk> sortedPerks = perks.stream().sorted(Comparator.comparingDouble(Perk::getCost)).toList();
        sortedPerks.forEach(perk -> {
            if (playerData.hasPerk(perk.getID())) {
                GuiItem guiItem = perk.createGUIItem(true);
                guiItem.setAction(event -> localeManager.sendMessage(viewer, "perk-already-owned", StringPlaceholders.single("perk", perk.getName())));
                gui.addItem(guiItem);
            } else {
                GuiItem guiItem = perk.createGUIItem(false);
                guiItem.setAction(event -> handlePerkPurchase(viewer, playerData, localeManager, perk));
                gui.addItem(guiItem);
            }
        });
        addNavigationItems(viewer, playerData);
        addResetItem(viewer, playerData, localeManager);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.setCloseGuiAction(event -> {
        });
    }

    private void handlePerkPurchase(Player viewer, PlayerData playerData, LocaleManager localeManager, Perk perk) {
        if (playerData.getPoints() >= perk.getCost()) {
            if (playerData.addPerk(perk.getID())) {
                DataManager dataManager = PlayerPerks.getInstance().getManager(DataManager.class);
                playerData.removePoints(perk.getCost());
                localeManager.sendMessage(viewer, "perk-bought", StringPlaceholders.single("perk", perk.getName()));
                perk.onPurchase(viewer);
                dataManager.addPerk(playerData.getUUID().toString(), perk.getID());
                openGUI(viewer);
            } else {
                localeManager.sendMessage(viewer, "perk-already-owned", StringPlaceholders.single("perk", perk.getName()));
            }
        } else {
            localeManager.sendMessage(viewer, "points-not-enough", StringPlaceholders.single("points", playerData.getPoints()));
        }
    }

    private void addNavigationItems(Player viewer, PlayerData playerData) {
        gui.setItem(6, 1, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
        gui.setItem(6, 2, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text("Previous")).asGuiItem(event -> gui.previous()));
        gui.setItem(6, 4, ItemBuilder.from(Material.EMERALD_BLOCK).name(Component.text("Perk Points: " + playerData.getPoints())).asGuiItem());
        gui.setItem(6, 5, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
        //6 is used by resetItem
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text("Next")).asGuiItem(event -> gui.next()));
        gui.setItem(6, 8, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
        gui.setItem(6, 9, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
    }

    private void addResetItem(Player viewer, PlayerData playerData, LocaleManager localeManager) {
        GuiItem resetItem = ItemBuilder.from(Material.REDSTONE_BLOCK).name(Component.text("Reset Perks")).asGuiItem();
        resetItem.setAction(event -> {
            if (viewer.hasPermission("playerperks.bypass.reset")) {
                playerData.resetPerks();
                localeManager.sendMessage(viewer, "perk-reset");
                openGUI(viewer);
            } else if (playerData.getCooldown() > Instant.now().getEpochSecond()) {
                localeManager.sendMessage(viewer, "reset-cooldown", StringPlaceholders.single("cooldown", Utils.calculateTime(playerData.getCooldown() - Instant.now().getEpochSecond())));
            } else {
                playerData.resetPerks();
                localeManager.sendMessage(viewer, "perk-reset");
                openGUI(viewer);
            }
            openGUI(viewer);
        });
        gui.setItem(6, 6, resetItem);
    }

    public void openGUI(Player player) {
        this.createGUI(player);
        gui.open(player);
    }

    @Override
    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
    }

    @Override
    public void disable() {

    }
}
