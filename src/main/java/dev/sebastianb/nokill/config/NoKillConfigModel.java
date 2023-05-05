package dev.sebastianb.nokill.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;

@Config(name = "nokill-config", wrapperName = "NoKillConfig")
public class NoKillConfigModel {
    @Nest
    public Discord discord = new Discord();
    public static class Discord {
        public String token = "REPLACE_ME";
        public String channelID = "0123456789";

    }

}
