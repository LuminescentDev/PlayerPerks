package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;

public class ConfigManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        BASE_POINTS("default-points", 10, "The default amount of points a player starts with"),
        MAX_PERKS("max-perks", 5, "The maximum amount of perks a player can have");

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
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public String[] getComments() {
            return new String[0];
        }

        @Override
        public Object getCachedValue() {
            return null;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return null;
        }

        @Override
        public void setCachedValue(Object o) {

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
