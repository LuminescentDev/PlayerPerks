package xyz.akiradev.playerperks;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.*;
import org.bukkit.Bukkit;
import xyz.akiradev.playerperks.events.PlayerEvents;
import xyz.akiradev.playerperks.managers.*;

import java.util.*;

public final class PlayerPerks extends RosePlugin {

    private static PlayerPerks instance;

    public PlayerPerks() {
        super(-1, -1, ConfigManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    protected void enable() {
        DataManager dataManager = this.getManager(DataManager.class);
        Bukkit.getOnlinePlayers().forEach(player -> dataManager.getPlayerData(player.getUniqueId(), data -> {}));
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
    }

    @Override
    protected void disable() {
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                DataManager.class,
                PerkManager.class,
                GUIManager.class
        );
    }

    public static PlayerPerks getInstance() {
        return instance;
    }
}
