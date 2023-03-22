package dev.sebastianb.nokillkillkillkill.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PVPStatusCommand implements ICommand {

    @Override
    public String commandName() {
        return "status";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return CommandManager.literal(commandName())
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) {

        return Command.SINGLE_SUCCESS;
    }

    static class PVPOnCommand implements ICommand {

        @Override
        public String commandName() {
            return "on";
        }

        @Override
        public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
            return CommandManager.literal(commandName())
                    .executes(this::execute);
        }

        private int execute(CommandContext<ServerCommandSource> context) {

            return Command.SINGLE_SUCCESS;
        }

    }

    static class PVPOffCommand implements ICommand {

        @Override
        public String commandName() {
            return "off";
        }

        @Override
        public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
            return CommandManager.literal(commandName())
                    .executes(this::execute);
        }

        private int execute(CommandContext<ServerCommandSource> context) {
            ServerPlayerEntity player = context.getSource().getPlayer();

            return Command.SINGLE_SUCCESS;
        }

    }

}
