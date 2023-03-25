package dev.sebastianb.nokillkillkillkill.command.challenge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sebastianb.nokillkillkillkill.SebaUtils;
import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import dev.sebastianb.nokillkillkillkill.command.ICommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

public class ChallengeCommand implements ICommand {

    public static HashMap<UUID, UUID> challengePlayerMap = new HashMap<>(); // map holding which players are actively challenging each-other

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
        try {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            ServerPlayerEntity challengedPlayer = EntityArgumentType.getPlayer(context, "player");

            if (sourcePlayer.equals(challengedPlayer)) {
                // if challenger is the source player, they can't challenge themselves
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.ran_as_self"));
            } else if (challengePlayerMap.containsKey(sourcePlayer.getUuid()) || challengePlayerMap.containsValue(sourcePlayer.getUuid())) {
                // if the player is currently in a challenge, they can't send another challenge
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.challenger_player_is_in_challenge"));
            } else if (challengePlayerMap.containsKey(challengedPlayer.getUuid()) || challengePlayerMap.containsValue(challengedPlayer.getUuid())) {
                // if the challenged player is currently in a challenge, you can't send a challenge to them
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.challenged_player_is_in_challenge", challengedPlayer.getName()));
            } else if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.contains(sourcePlayer.getUuid())) {
                // if challenger is currently challenging another player
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.command_running"));
            } else {
                if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.containsValue(challengedPlayer.getUuid())) {
                    // removes source player from challenge if they accepted their challenge
                    ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.remove(sourcePlayer.getUuid());
                    // sends message to each player letting them know request has been accepted.
                    sourcePlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.accepted_received", challengedPlayer.getName()));
                    challengedPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.accepted_sent", sourcePlayer.getName()));

                    // adds players to hashmap
                    challengePlayerMap.put(challengedPlayer.getUuid(), sourcePlayer.getUuid());

                    // broadcast message to each player saying a duel has started
                    // TODO: configurable if this should announce to only the players or everyone
                    MinecraftServer server = context.getSource().getServer();
                    server.getPlayerManager().getPlayerList().forEach(player -> {
                        player.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.broadcast", challengedPlayer.getName(), sourcePlayer.getName()));
                    });
                    // if pvp is off for the player, just send a message saying it is enabled, it's handled in the PVP mixin
                    // TODO: clean this up and put into it's own helper method probably
                    if (!NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(challengedPlayer)) {
                        challengedPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.temp_enabled"));
                    }
                    if (!NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(sourcePlayer)) {
                        sourcePlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.temp_enabled"));
                    }
                    // set challenge state to true for both players
                    NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(sourcePlayer, true);
                    NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(challengedPlayer, true);

                } else {
                    // send message to source player saying they've challenged the player.
                    SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_sent", challengedPlayer.getName()));

                    // send message to challenged player letting them know to accept the challenge.
                    // TODO: make command clickable and display a hover text with the command
                    challengedPlayer.sendMessage(
                            Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_received", sourcePlayer.getName(),
                                    Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_player_received.click_here")
                            )
                    );
                    ChallengeInviteTimer.runChallengeSchedule(challengedPlayer, sourcePlayer, 30);
                }
            }

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return Command.SINGLE_SUCCESS;
    }


}
