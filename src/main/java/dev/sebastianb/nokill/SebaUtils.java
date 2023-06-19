package dev.sebastianb.nokill;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SebaUtils {

    public static class ChatUtils {

        public static void sayEmptyMessage(CommandContext<ServerCommandSource> context) {
            context.getSource().sendFeedback(Text::empty, false);
        }

        public static void saySimpleMessage(CommandContext<ServerCommandSource> context, Text message) {
            saySimpleMessage(context, message, false);
        }

        public static void saySimpleMessage(CommandContext<ServerCommandSource> context, Text message, boolean broadcastToOps) {
            context.getSource().sendFeedback(() -> message, broadcastToOps);
        }

    }


}
