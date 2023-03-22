package dev.sebastianb.nokillkillkillkill;

import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import dev.sebastianb.nokillkillkillkill.command.NKKKKCommand;
import net.fabricmc.api.ModInitializer;

import java.util.logging.Logger;

public class NoKillKillKillKill implements ModInitializer {

    public static final Logger LOGGER = Logger.getLogger("nokillkillkillkill");

    @Override
    public void onInitialize() {

        NKKKKCommand.register();
        NKKKKAbilities.register();

    }

}
