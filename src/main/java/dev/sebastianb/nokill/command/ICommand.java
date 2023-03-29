package dev.sebastianb.nokill.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {

    String commandName();

    LiteralArgumentBuilder<ServerCommandSource> registerNode();

}
