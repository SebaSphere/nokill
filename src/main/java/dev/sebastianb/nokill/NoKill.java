package dev.sebastianb.nokill;

import dev.sebastianb.nokill.ability.NoKillAbilities;
import dev.sebastianb.nokill.command.NoKillCommand;
import dev.sebastianb.nokill.config.NoKillConfig;
import dev.sebastianb.nokill.config.NoKillConfigModel;
import dev.sebastianb.nokill.jda.BotUtils;
import net.fabricmc.api.ModInitializer;

import java.util.logging.Logger;

public class NoKill implements ModInitializer {
    public static final String MOD_ID = "nokill";
    public static final Logger LOGGER = Logger.getLogger("nokill");
    public static final NoKillConfig CONFIG = NoKillConfig.createAndLoad();


    @Override
    public void onInitialize() {

        BotUtils.registerJDA(CONFIG.discord.token());
        NoKillCommand.register();
        NoKillAbilities.register();

    }

}
