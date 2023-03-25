package dev.sebastianb.nokillkillkillkill.command.challenge;

import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerPair(ServerPlayerEntity challenger, ServerPlayerEntity opponent) {
    public boolean contains(ServerPlayerEntity player) {
        return (player == challenger || player == opponent);
    }
}
