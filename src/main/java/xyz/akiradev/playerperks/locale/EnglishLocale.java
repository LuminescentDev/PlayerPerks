package xyz.akiradev.playerperks.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {
    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "AkiraDev";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&8&l[<g:#e41101:#1b34b8>PlayerPerks&8&l] ");

            this.put("#1", "Command Responses");
            this.put("no-permission-command", "You do not have permission to use this command.");
            this.put("unknown-command", "Unknown command. Use /playerperks help for help.");
            this.put("not-player", "You must be a player to use this command.");
            this.put("specify-player", "You must specify a player.");
            this.put("reset-cooldown", "You are still on cooldown for %cooldown%");

            this.put("#2", "Help Messages");
            this.put("command-help-description", "Shows this help info.");
            this.put("command-reload-description", "Reloads the plugin.");
            this.put("command-perks-points-description", "Get/Give/Remove/Set points of a player.");

            this.put("#3", "Perk Messages");
            this.put("perk-not-found", "Could not find perk %perk%.");
            this.put("perk-already-owned", "You already own %perk%.");
            this.put("perk-bought", "You have bought %perk%.");
            this.put("perk-reset", "Perks successfully reset.");

            this.put("#4", "Points Messages");
            this.put("points", "%player% has %points% points.");
            this.put("points-you-get", "You get %points% points.");
            this.put("points-not-enough", "You do not have enough points.");
            this.put("points-added", "%player% has been given %points% points.");
            this.put("points-removed", "%player% has had %points% removed.");
            this.put("points-set", "%player% points has been set to %points%.");
            this.put("points-not-added", "Could not add %points% points to %player%.");
            this.put("points-not-removed", "Could not remove %points% points from %player%.");
            this.put("points-not-set", "Could not set %player%'s points to %points%.");
            this.put("points-too-high", "You cannot set %player% to %points% points because they are higher than the configured max, points have been set to configured max.");

        }};
    }
}
