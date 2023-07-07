package xyz.akiradev.playerperks.command.commands;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.command.BaseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.akiradev.playerperks.managers.PerksGUIManager;
import xyz.akiradev.playerperks.managers.LocaleManager;

public class CommandPerks extends BaseCommand {

    public CommandPerks(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!(context.getSender() instanceof Player player)) {
            locale.sendMessage(context.getSender(), " ");
            return;
        }
        this.rosePlugin.getManager(PerksGUIManager.class).openGUI((player));
    }

    @Override
    public String getRequiredPermission(){
        return "playerperks.use";
    }
}
