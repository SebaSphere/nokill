package dev.sebastianb.nokillkillkillkill.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sebastianb.nokillkillkillkill.command.challenge.ChallengeCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;

public class NKKKKCommand {

    private static final ArrayList<ICommand> commands = new ArrayList<>();

    private static final String[] commandLiterals = new String[]{"pvp", "p"};


    public static void register() {

        commands.add(new ChallengeCommand());

        commands.add(new PVPStatusCommand());
        commands.add(new PVPStatusCommand.PVPOnCommand());
        commands.add(new PVPStatusCommand.PVPOffCommand());

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

    protected static ArrayList<ICommand> getCommands() {
        return commands;
    }

}