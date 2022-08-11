package xyz.akiradev.playerperks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.akiradev.playerperks.PlayerPerks;
import xyz.akiradev.playerperks.managers.DataManager;

public class PlayerEvents implements Listener {

    private final DataManager dataManager;

    public PlayerEvents(PlayerPerks plugin) {
        this.dataManager = plugin.getManager(DataManager.class);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoined(PlayerJoinEvent event) {
        dataManager.getPlayerData(event.getPlayer().getUniqueId(), (playerData) -> {});
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dataManager.getPlayerData(player.getUniqueId()).save();
        dataManager.unloadPlayerData(player.getUniqueId());
    }

}
