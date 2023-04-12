package dev.sebastianb.nokillkillkillkill.command.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerPair(ServerPlayerEntity challenger, ServerPlayerEntity opponent) {
    public boolean contains(ServerPlayerEntity player) {
        return (player == challenger || player == opponent);
    }

    // For all intents and purposes, a pair should be equal if it contains both of the same players
    @Override
    public int hashCode() {
        return challenger.hashCode() + opponent.hashCode();
    }
}
