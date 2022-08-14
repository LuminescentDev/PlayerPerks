package xyz.akiradev.playerperks.command.commands;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.akiradev.playerperks.PlayerPerks;
import xyz.akiradev.playerperks.managers.ConfigManager;
import xyz.akiradev.playerperks.managers.DataManager;
import xyz.akiradev.playerperks.managers.LocaleManager;

public class CommandPerksPoints extends RoseCommand {


    public CommandPerksPoints(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional Player player, @Optional RoseSubCommand subCommand) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        CommandSender sender = context.getSender();

        if(!(sender instanceof Player) && player == null){
            locale.sendMessage(sender, "specify-player");
            return;
        }

        Player target = player == null ? (Player) sender : player;

        locale.sendMessage(sender, "points", StringPlaceholders.builder("player", target.getDisplayName()).addPlaceholder("points", PlayerPerks.getInstance().getManager(DataManager.class).getPlayerData(target.getUniqueId()).getPoints()).build());

    }

    public static class SubCommandGive extends RoseSubCommand {

        public SubCommandGive(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Inject Player player, int points) {
            final DataManager dataManager = PlayerPerks.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            if (dataManager.getPlayerData(player.getUniqueId()).addPoints(points)){
                locale.sendMessage(context.getSender(), "points-added", StringPlaceholders.builder("player", player.getDisplayName()).addPlaceholder("points", points).build());
            } else {
                locale.sendMessage(context.getSender(), "points-not-added", StringPlaceholders.builder("player", player.getDisplayName()).addPlaceholder("points", points).build());
            }
        }

        @Override
        protected String getDefaultName() {
            return "give";
        }

        @Override
        public String getRequiredPermission(){
            return "playerperks.points.give";
        }

    }

    public static class SubCommandRemove extends RoseSubCommand {

        public SubCommandRemove(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Inject Player player, int points) {
            final DataManager dataManager = PlayerPerks.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            if (dataManager.getPlayerData(player.getUniqueId()).removePoints(points)){
                locale.sendMessage(context.getSender(), "points-removed", StringPlaceholders.builder("player", player.getDisplayName()).addPlaceholder("points", points).build());
            } else {
                locale.sendMessage(context.getSender(), "points-not-removed", StringPlaceholders.builder("player", player.getDisplayName()).addPlaceholder("points", points).build());
            }
        }

        @Override
        protected String getDefaultName() {
            return "remove";
        }

        @Override
        public String getRequiredPermission(){
            return "playerperks.points.remove";
        }

    }

    public static class SubCommandSet extends RoseSubCommand {

        public SubCommandSet(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Inject Player player, int points) {
            final DataManager dataManager = PlayerPerks.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            if(ConfigManager.Setting.MAX_POINTS.getInt() < points){
                locale.sendMessage(context.getSender(), "points-too-high", StringPlaceholders.builder("player", player.getDisplayName()).addPlaceholder("points", points).build());
            }
            dataManager.getPlayerData(player.getUniqueId()).setPoints(points);
        }

        @Override
        protected String getDefaultName() {
            return "set";
        }

        @Override
        public String getRequiredPermission(){
            return "playerperks.points.set";
        }

    }

    @Override
    protected String getDefaultName() {
        return "points";
    }

    @Override
    public String getDescriptionKey() {
        return "command-perks-points-description";
    }

    @Override
    public String getRequiredPermission(){
        return "playerperks.points";
    }

}
