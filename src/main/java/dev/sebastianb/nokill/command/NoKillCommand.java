package dev.sebastianb.nokill.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sebastianb.nokill.command.challenge.ChallengeCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class NoKillCommand {

    private static final ICommand[] commands = {
            new ChallengeCommand(),
            new PVPStatusCommand(),
            // new PVPStatusCommand.PVPOnCommand()
            // new PVPStatusCommand.PVPOffCommand()
    };

    private static final String[] commandLiterals = new String[]{"pvp", "p"};


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            for (ICommand command : commands) {
                for (String literal : commandLiterals) {
                    LiteralArgumentBuilder<ServerCommandSource> builder =
                            CommandManager.literal(literal)
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .then(command.registerNode());
                    dispatcher.register(builder);
                }
            }
        });
    }

    protected static ICommand[] getCommands() {
        return commands;
    }

}