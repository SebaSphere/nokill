package dev.sebastianb.nokillkillkillkill.command.challenge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sebastianb.nokillkillkillkill.SebaUtils;
import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import dev.sebastianb.nokillkillkillkill.command.ICommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class ChallengeCommand implements ICommand {

    public static ArrayList<PlayerPair> playerPairs = new ArrayList<>(); // list holding current duels

    /**
     * Will get the pair this UUID is contained in, optional will be empty if such pair doesn't exist
     */
    public static Optional<PlayerPair> findInPairs(ServerPlayerEntity player) {
        for (var pair : playerPairs)
            if (pair.contains(player)) return Optional.of(pair);

        return Optional.empty();
    }

    /**
     * Returns true if the given UUID is in any challenge currently (in playerPairs)
     */
    public static boolean containsInPairs(ServerPlayerEntity player) {
        return findInPairs(player).isPresent();
    }

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

            assert sourcePlayer != null;
            if (sourcePlayer.equals(challengedPlayer)) {
                // if challenger is the source player, they can't challenge themselves
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.ran_as_self"));
                return Command.SINGLE_SUCCESS;
            }

            var isSourceInPair = containsInPairs(sourcePlayer);
            var isChallengedInPair = containsInPairs(challengedPlayer);

            if (isSourceInPair) { // source player is already in a challenge
                SebaUtils.ChatUtils.saySimpleMessage(
                        context,
                        Text.translatable("nokillkillkillkill.command.pvp.challenge.challenger_player_is_in_challenge")
                );
            } else if (isChallengedInPair) { // challenged player is already in a challenge
                var challengedName = challengedPlayer.getName();
                SebaUtils.ChatUtils.saySimpleMessage(
                        context,
                        Text.translatable("nokillkillkillkill.command.pvp.challenge.challenged_player_is_in_challenge", challengedName)
                );
            } else if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.contains(sourcePlayer.getUuid())) {
                // if challenger is currently challenging another player
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.challenge.command_running"));
            } else if (ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.containsValue(challengedPlayer.getUuid())) {
                acceptChallenge(context, sourcePlayer, challengedPlayer);
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

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void acceptChallenge(
            CommandContext<ServerCommandSource> context,
            ServerPlayerEntity sourcePlayer,
            ServerPlayerEntity challengedPlayer
    ) {
        var sourceName = sourcePlayer.getName();
        var challengedName = sourcePlayer.getName();

        // removes source player from challenge if they accepted their challenge
        ChallengeInviteTimer.invitedPlayerAndSourcePlayerUUID.remove(sourcePlayer.getUuid());
        // sends message to each player letting them know request has been accepted.
        sourcePlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.accepted_received", challengedName));
        challengedPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.accepted_sent", sourceName));

        // add new challenge pair
        playerPairs.add(new PlayerPair(challengedPlayer, sourcePlayer));

        // broadcast message to each player saying a duel has started
        // TODO: configurable if this should announce to only the players or everyone
        var server = context.getSource().getServer();
        server.getPlayerManager()
                .getPlayerList()
                .forEach(player ->player.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.broadcast", challengedName, sourceName)));

        announceChallengeBegin(sourcePlayer, challengedPlayer);
    }

    private static void announceChallengeBegin(ServerPlayerEntity sourcePlayer, ServerPlayerEntity challengedPlayer) {
        // if pvp is off for the player, just send a message saying it is enabled, it's handled in the PVP mixin
        if (!NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(challengedPlayer)) {
            challengedPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.temp_enabled"));
        }
        if (!NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(sourcePlayer)) {
            sourcePlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.temp_enabled"));
        }
        // set challenge state to true for both players
        NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(sourcePlayer, true);
        NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(challengedPlayer, true);
    }
}
