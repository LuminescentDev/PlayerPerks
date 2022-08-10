package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.akiradev.playerperks.Perk;
import xyz.akiradev.playerperks.PlayerPerks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PerkManager extends Manager {

    private static PerkManager instance;

    private final Map<String, Perk> perks = new HashMap<>();
    private final Map<String, Perk> perIDs = new HashMap<>();

    public PerkManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        instance = this;
    }

    public void loadPerkConfig(Perk perk) {

    }

    public void registerPerk(Perk perk) {
        instance.perks.put(perk.getName(), perk);
        instance.perIDs.put(perk.getID(), perk);
        PlayerPerks.getInstance().getLogger().info("Registering Perk: " + perk.getName());
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
