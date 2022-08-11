package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.akiradev.playerperks.PlayerPerks;

public class ConfigManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        BASE_POINTS("default-points", 10, "The default amount of points a player starts with"),
        MAX_PERKS("max-perks", 5, "The maximum amount of perks a player can have"),
        MAX_POINTS("max-points", 100, "The maximum amount of points a player can have. 0 to disable"),
        PERK_RESET_COOLDOWN("perk-reset-cooldown", null, "The cooldown in seconds between resetting a player's perks"),
        PERK_RESET_COOLDOWN_DAYS("perk-reset-cooldown.days", 1),
        PERK_RESET_COOLDOWN_HOURS("perk-reset-cooldown.hours", 0),
        PERK_RESET_COOLDOWN_MINUTES("perk-reset-cooldown.minutes", 0),
        PERK_RESET_COOLDOWN_SECONDS("perk-reset-cooldown.seconds", 0);
        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return PlayerPerks.getInstance().getManager(ConfigManager.class).getConfig();
        }
    }

    public ConfigManager(RosePlugin plugin) {
        super(plugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[0];
    }
}
