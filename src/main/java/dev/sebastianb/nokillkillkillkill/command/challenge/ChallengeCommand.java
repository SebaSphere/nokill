package dev.sebastianb.nokillkillkillkill.command.challenge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sebastianb.nokillkillkillkill.SebaUtils;
import dev.sebastianb.nokillkillkillkill.command.ICommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChallengeCommand implements ICommand {
    @Override
    public String commandName() {
        return "challenge";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return CommandManager.literal(commandName())
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(ChallengeCommand::challengePlayer));
    }

    private static int challengePlayer(CommandContext<ServerCommandSource> context) {
        // TODO: do logic for challenging a player for PVP
        // TODO: check if the challenged player currently has a pending challenge
        try {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            ServerPlayerEntity challengedPlayer = EntityArgumentType.getPlayer(context, "player");

            if (sourcePlayer.equals(challengedPlayer)) {
                // if challenger is the source player, they can't challenge themselves
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.ran_as_self"));
            } else if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.contains(sourcePlayer.getUuid())) {
                // if challenger is currently challenging another player
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.command_running"));
            } else {
                if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.containsValue(challengedPlayer.getUuid())) {
                    // removes source player from challenge if they accepted their challenge
                    ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.remove(sourcePlayer.getUuid());
                    // TODO: do challenge logic here and enable pvp for both players

                } else {
                    // send message to source player saying they've challenged the player.
                    SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_sent", challengedPlayer.getName()));

                    // send message to challenged player letting them know to accept the challenge.
                    // TODO: make command clickable
                    challengedPlayer.sendMessage(
                            Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_received", sourcePlayer.getName(),
                                    Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_received.click_here")
                            )
                    );
                    ChallengeInviteTimer.runThread(challengedPlayer, sourcePlayer, 30);
                }
            }

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return Command.SINGLE_SUCCESS;
    }


}
