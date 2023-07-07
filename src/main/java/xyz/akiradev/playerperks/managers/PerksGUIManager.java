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

public class PerksGUIManager extends Manager {

    private static PaginatedGui gui;
    private boolean perksPositive = true;
    private SHOW showToggle = SHOW.ALL;
    private final DataManager dataManager;
    private final LocaleManager localeManager;
    private final PerkManager perkManager;

    private enum SHOW {
        ALL,
        OWNED,
        UNOWNED
    }

    public PerksGUIManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        PlayerPerks playerPerks = PlayerPerks.getInstance();
        this.dataManager = playerPerks.getManager(DataManager.class);
        this.localeManager = playerPerks.getManager(LocaleManager.class);
        this.perkManager = playerPerks.getManager(PerkManager.class);
    }

    public void createGUI(Player viewer) {
        PlayerData playerData = dataManager.getPlayerData(viewer.getUniqueId());
        gui = Gui.paginated()
                .title(Component.text(getTitle()))
                .rows(6)
                .create();

        Collection<Perk> perks = perkManager.getPerks();
        List<Perk> sortedPerks = perks.stream().sorted(Comparator.comparingDouble(Perk::getCost)).toList();

        List<Perk> filteredPerks;
        switch (showToggle) {
            case OWNED -> filteredPerks = sortedPerks.stream().filter(perk -> playerData.hasPerk(perk.getID())).toList();
            case UNOWNED -> filteredPerks = sortedPerks.stream().filter(perk -> !playerData.hasPerk(perk.getID())).toList();
            default -> filteredPerks = sortedPerks;
        }

        if (perksPositive) {
            filteredPerks = filteredPerks.stream().filter(perk -> perk.getCost() > 0).toList();
        } else {
            filteredPerks = filteredPerks.stream().filter(perk -> perk.getCost() < 0).toList();
        }

        filteredPerks.forEach(perk -> {
            GuiItem guiItem;
            if (playerData.hasPerk(perk.getID())) {
                guiItem = perk.createGUIItem(true);
                guiItem.setAction(event -> localeManager.sendMessage(viewer, "perk-already-owned", StringPlaceholders.single("perk", perk.getName())));
            } else {
                guiItem = perk.createGUIItem(false);
                guiItem.setAction(event -> handlePerkPurchase(viewer, playerData, perk));
            }
            gui.addItem(guiItem);
        });

        addNavigationItems(viewer, playerData);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.setCloseGuiAction(event -> {});
    }

    private String getTitle() {
        return perksPositive ?
                localeManager.getLocaleMessage("gui-positive-title") :
                localeManager.getLocaleMessage("gui-negative-title");
    }

    private void handlePerkPurchase(Player viewer, PlayerData playerData, Perk perk) {
        if (playerData.getPoints() >= perk.getCost()) {
            if (playerData.addPerk(perk.getID())) {
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

        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER)
                .name(Component.text(localeManager.getLocaleMessage("gui-previous")))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(6, 4, ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(Component
                        .text(localeManager
                                .getLocaleMessage("gui-points", StringPlaceholders.single("points", playerData.getPoints()))
                        )
                )
                .model(ConfigManager.Setting.GUI_ITEM_MODELS_POINTS.getInt())
                .asGuiItem());

        addTypeSwitchItem(viewer);
        addResetItem(viewer, playerData);

        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER)
                .name(Component.text(localeManager.getLocaleMessage("gui-next")))
                .asGuiItem(event -> gui.next()));

        gui.setItem(6, 8, ItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
        addOwnedToggleItem(viewer);
    }

    private void addTypeSwitchItem(Player viewer) {
        Material typeSwitchMaterial;
        String typeSwitchName;
        int typeSwitchModel;
        if (this.perksPositive) {
            typeSwitchMaterial = Material.RED_STAINED_GLASS_PANE;
            typeSwitchName = localeManager.getLocaleMessage("gui-show-negative-perks");
            typeSwitchModel = ConfigManager.Setting.GUI_ITEM_MODELS_POSITIVE_PERKS.getInt();
        } else {
            typeSwitchMaterial = Material.GREEN_STAINED_GLASS_PANE;
            typeSwitchName = localeManager.getLocaleMessage("gui-show-positive-perks");
            typeSwitchModel = ConfigManager.Setting.GUI_ITEM_MODELS_NEGATIVE_PERKS.getInt();
        }
        GuiItem typeSwitchItem = ItemBuilder
                .from(typeSwitchMaterial)
                .name(Component.text(typeSwitchName))
                .model(typeSwitchModel)
                .asGuiItem();
        typeSwitchItem.setAction(event -> {
            this.perksPositive = !this.perksPositive;
            openGUI(viewer);
        });
        gui.setItem(6, 5, typeSwitchItem);
    }

    private void addOwnedToggleItem(Player viewer) {
        Material ownedToggleMaterial;
        String ownedToggleName;
        int ownedToggleModel;

        switch (showToggle) {
            case ALL -> {
                ownedToggleMaterial = Material.YELLOW_STAINED_GLASS_PANE;
                ownedToggleName = localeManager.getLocaleMessage("gui-all-perks");
                ownedToggleModel = ConfigManager.Setting.GUI_ITEM_MODELS_SHOW_ALL_PERKS.getInt();
            }
            case OWNED -> {
                ownedToggleMaterial = Material.RED_STAINED_GLASS_PANE;
                ownedToggleName = localeManager.getLocaleMessage("gui-owned-perks");
                ownedToggleModel = ConfigManager.Setting.GUI_ITEM_MODELS_SHOW_OWNED_PERKS.getInt();
            }
            default -> {
                ownedToggleMaterial = Material.GREEN_STAINED_GLASS_PANE;
                ownedToggleName = localeManager.getLocaleMessage("gui-unowned-perks");
                ownedToggleModel = ConfigManager.Setting.GUI_ITEM_MODELS_SHOW_UNOWNED_PERKS.getInt();
            }
        }

        GuiItem ownedToggleItem = ItemBuilder
                .from(ownedToggleMaterial)
                .name(Component.text(ownedToggleName))
                .model(ownedToggleModel)
                .asGuiItem();

        ownedToggleItem.setAction(event -> {
            switch (showToggle) {
                case ALL -> showToggle = SHOW.OWNED;
                case OWNED -> showToggle = SHOW.UNOWNED;
                case UNOWNED -> showToggle = SHOW.ALL;
            }
            openGUI(viewer);
        });

        gui.setItem(6, 9, ownedToggleItem);
    }

    private void addResetItem(Player viewer, PlayerData playerData) {
        GuiItem resetItem = ItemBuilder
                .from(Material.REDSTONE_BLOCK)
                .name(Component.text(localeManager.getLocaleMessage("gui-reset-perks")))
                .model(ConfigManager.Setting.GUI_ITEM_MODELS_RESET_PERKS.getInt())
                .asGuiItem();
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
