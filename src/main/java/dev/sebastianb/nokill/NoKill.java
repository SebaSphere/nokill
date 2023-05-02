package dev.sebastianb.nokill;

import dev.sebastianb.nokill.ability.NoKillAbilities;
import dev.sebastianb.nokill.command.NoKillCommand;
import net.fabricmc.api.ModInitializer;

import java.util.logging.Logger;

public class NoKill implements ModInitializer {
    public static final String MOD_ID = "nokill";
    public static final Logger LOGGER = Logger.getLogger("nokill");

    @Override
    public void onInitialize() {

        NoKillCommand.register();
        NoKillAbilities.register();

    }

}
