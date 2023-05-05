package dev.sebastianb.nokill.jda;

import dev.sebastianb.nokill.NoKill;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.logging.Level;

public class BotUtils {

    private static JDA jda; // assuming you have initialized the JDA instance somewhere

    public static void registerJDA(String token) {
        try {
            jda = JDABuilder.createDefault(token).build();
            jda.awaitReady();
        } catch (InterruptedException e) {
            NoKill.LOGGER.log(Level.WARNING, "Bot token not found! Define one in config.");
            e.printStackTrace();
            // bad idea but "lol" we crash
            System.exit(-1);
        }
    }

    public static void sendEmbeddedMessage(EmbedBuilder builder, MessageChannel channel) {
        if (channel != null) {
            channel.sendMessageEmbeds(builder.build()).queue();
        } else {
            NoKill.LOGGER.log(Level.WARNING, "Channel not found! Define one in config.");
        }
    }

    public static MessageChannel getTextChannelById(String channelId) {
        MessageChannel channel = jda.getTextChannelById(channelId);
        if (channel!= null) {
            return jda.getTextChannelById(channelId);
        } else {
            NoKill.LOGGER.log(Level.WARNING, "Channel not found! Define one in config.");
            // bad idea but "lol" we crash
            System.exit(-1);
            return null;
        }
    }

}
