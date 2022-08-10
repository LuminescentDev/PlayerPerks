package xyz.akiradev.playerperks.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class PerkCommandWrapper extends RoseCommandWrapper {

    public PerkCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "perks";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("pperks", "playerperks");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.akiradev.playerperks.command.commands");
    }

    @Override
    public boolean includeBaseCommand() {
        return false;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }
}
