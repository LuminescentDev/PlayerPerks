package xyz.akiradev.playerperks.defaultPerks;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import xyz.akiradev.playerperks.Perk;

public class PerkNoHunger extends Perk {

    public PerkNoHunger() {
        super(Material.COOKED_BEEF, "No Hunger", "You will not be hungry while wearing this perk.", "no-hunger", 0);
    }


    @Override
    public void onPurchase(HumanEntity player) {
        player.sendMessage("You have purchased the No Hunger perk.");
    }
}
