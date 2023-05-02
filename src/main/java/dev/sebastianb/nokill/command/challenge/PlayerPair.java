package dev.sebastianb.nokill.command.challenge;

import dev.sebastianb.nokill.state.ChallengesState;
import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerPair(ServerPlayerEntity challenger, ServerPlayerEntity opponent) {
    public boolean contains(ServerPlayerEntity player) {
        return (player == challenger || player == opponent);
    }

    /** Increase the challenges attempted value of both players in pair by 1 */
    public void increaseChallengeAttempts() {
        ChallengesState.doWithPlayerState(challenger, playerState -> playerState.challengeAttempts++);
        ChallengesState.doWithPlayerState(opponent, playerState -> playerState.challengeAttempts++);
    }

    // For all intents and purposes, a pair should be equal if it contains both of the same players
    @Override
    public int hashCode() {
        return challenger.hashCode() + opponent.hashCode();
    }
}
