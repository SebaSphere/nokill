package dev.sebastianb.nokill.command.challenge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sebastianb.nokill.SebaUtils;
import dev.sebastianb.nokill.ability.NoKillAbilities;
import dev.sebastianb.nokill.command.ICommand;
import dev.sebastianb.nokill.command.challenge.pairstructs.PlayerPairList;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChallengeCommand implements ICommand {
    public static PlayerPairList challenges = new PlayerPairList(); // list holding current duels

    @Override
    public String commandName() {
        return "challenge";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return CommandManager.literal(commandName())
                .requires(serverCommandSource -> Permissions.check(serverCommandSource, "nokill.challenge"))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(ChallengeCommand::challengePlayer));
    }

    private static int challengePlayer(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            ServerPlayerEntity challengedPlayer = EntityArgumentType.getPlayer(context, "player");

            if (sourcePlayer == null) { // sent by server (I think?)
                context.getSource().sendMessage(Text.translatable("nokill.command.pvp.no_source_player"));
            } else if (sourcePlayer.equals(challengedPlayer)) { // prevent player challenging themselves
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokill.command.pvp.challenge.ran_as_self"));
            } else if (challenges.contains(sourcePlayer)) { // source player is already in a duel
                SebaUtils.ChatUtils.saySimpleMessage(
                        context,
                        Text.translatable("nokill.command.pvp.challenge.challenger_player_is_in_challenge")
                );
            } else if (challenges.contains(challengedPlayer)) { // challenged player is already in a duel
                var challengedName = challengedPlayer.getName();
                SebaUtils.ChatUtils.saySimpleMessage(
                        context,
                        Text.translatable("nokill.command.pvp.challenge.challenged_player_is_in_challenge", challengedName)
                );
            } else {
                attemptToChallenge(context, sourcePlayer, challengedPlayer);
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void attemptToChallenge(
            CommandContext<ServerCommandSource> context,
            ServerPlayerEntity sourcePlayer,
            ServerPlayerEntity challengedPlayer
    ) {
        var maybePair = ChallengeInviteTimer.playerInvites.find(challengedPlayer);

        if (maybePair.isPresent()) {
            var pair = maybePair.get();

            if (pair.contains(sourcePlayer)) {
                acceptChallenge(context, pair);
            } else {
                // either player has already challenged someone else
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokill.command.pvp.challenge.command_running"));
            }
            return;
        }

        // send message to source player saying they've challenged the player.
        SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokill.command.pvp.challenge.invite_player_sent", challengedPlayer.getName()));

        // send message to challenged player letting them know to accept the challenge.
        // TODO: make command clickable and display a hover text with the command
        challengedPlayer.sendMessage(
                Text.translatable("nokill.command.pvp.challenge.invite_player_received", sourcePlayer.getName(),
                        Text.translatable("nokill.command.pvp.challenge.invite_player_received.click_here"))
        );
        // let player know how to accept
        challengedPlayer.sendMessage(Text.translatable("nokill.command.pvp.challenge.invite_response_info", sourcePlayer.getName()));

        ChallengeInviteTimer.createInvite(sourcePlayer, challengedPlayer);
    }

    private static void acceptChallenge(CommandContext<ServerCommandSource> context, PlayerPair pair) {
        var challengerName = pair.challenger().getName();
        var opponentName = pair.opponent().getName();

        ChallengeInviteTimer.playerInvites.remove(pair);
        // sends message to each player letting them know request has been accepted.
        pair.challenger().sendMessage(Text.translatable("nokill.command.pvp.challenge.accepted_sent", opponentName));
        pair.opponent().sendMessage(Text.translatable("nokill.command.pvp.challenge.accepted_received", challengerName));

        challenges.add(pair);
        // broadcast message to each player saying a duel has started
        // TODO: configurable if this should announce to only the players or everyone
        var server = context.getSource().getServer();

        // broadcast to everyone out of the challenge (since they get their own messages above)
        for (var p : server.getPlayerManager().getPlayerList())  {
            if (pair.contains(p)) continue;
            p.sendMessage(Text.translatable("nokill.command.pvp.challenge.broadcast", challengerName, opponentName));
        }

        setPVPStates(pair);
    }

    private static void setPVPStates(PlayerPair pair) {
        // if pvp is off for the player, just send a message saying it is enabled, it's handled in the PVP mixin
        if (!NoKillAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(pair.opponent())) {
            pair.opponent().sendMessage(Text.translatable("nokill.command.pvp.temp_enabled"));
        }
        if (!NoKillAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(pair.challenger())) {
            pair.challenger().sendMessage(Text.translatable("nokill.command.pvp.temp_enabled"));
        }
        // set challenge state to true for both players
        NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(pair.challenger(), true);
        NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(pair.opponent(), true);
    }
}
