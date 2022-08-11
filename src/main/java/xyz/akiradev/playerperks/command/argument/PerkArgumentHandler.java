package xyz.akiradev.playerperks.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.akiradev.playerperks.Perk;
import xyz.akiradev.playerperks.managers.PerkManager;

import java.util.Collection;
import java.util.List;

public class PerkArgumentHandler extends RoseCommandArgumentHandler<Perk> {

    public PerkArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Perk.class);
    }

    @Override
    protected Perk handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        Perk perk = this.rosePlugin.getManager(PerkManager.class).getPerk(input);
        if (perk == null) {
            throw new HandledArgumentException("perk-not-found", StringPlaceholders.single("input", input));
        }
        return perk;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        Collection<Perk> perks = this.rosePlugin.getManager(PerkManager.class).getPerks();
        if(perks.isEmpty()) {
            return List.of("<No perks found>");
        }
        return List.of(perks.stream().map(Perk::getName).toArray(String[]::new));
    }
}