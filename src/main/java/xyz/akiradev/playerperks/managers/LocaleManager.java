package xyz.akiradev.playerperks.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.locale.Locale;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import xyz.akiradev.playerperks.locale.EnglishLocale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocaleManager extends AbstractLocaleManager {
    private List<String> translationLocales;

    public LocaleManager(RosePlugin plugin) {
        super(plugin);

        this.translationLocales = new ArrayList<>();
    }

    @Override
    public List<Locale> getLocales() {
        return Arrays.asList(
                new EnglishLocale()
        );
    }

}
