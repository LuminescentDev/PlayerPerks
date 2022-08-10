package xyz.akiradev.playerperks.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;

import java.util.List;

public class ArgumentHandler extends RoseCommandArgumentHandler {

    public ArgumentHandler(RosePlugin rosePlugin, Class handledType) {
        super(rosePlugin, handledType);
    }

    @Override
    protected Object handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        return null;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        return null;
    }
}
