package xyz.akiradev.playerperks;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.*;
import org.bukkit.Bukkit;
import xyz.akiradev.playerperks.defaultPerks.PerkNoHunger;
import xyz.akiradev.playerperks.managers.*;

import java.util.*;

public final class PlayerPerks extends RosePlugin {

    private static PlayerPerks instance;

    public PlayerPerks() {
        super(-1, 0, ConfigManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    protected void enable() {
        registerPerks();
        DataManager dataManager = this.getManager(DataManager.class);
        Bukkit.getOnlinePlayers().forEach(player -> {
            dataManager.getPlayerData(player.getUniqueId(), data -> {});
        });

        this.getManager(GUIManager.class).createGUI();
    }

    @Override
    protected void disable() {

    }

    private void registerPerks(){
        PerkManager perkManager = this.getManager(PerkManager.class);
        perkManager.registerPerk(new PerkNoHunger());
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                DataManager.class,
                PerkManager.class
        );
    }

    public static PlayerPerks getInstance() {
        return instance;
    }
}
