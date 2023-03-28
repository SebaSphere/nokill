package dev.sebastianb.nokillkillkillkill.command.challenge.pairstructs;

import dev.sebastianb.nokillkillkillkill.command.challenge.PlayerPair;
import net.minecraft.server.network.ServerPlayerEntity;

public class InvitePair {
    public final PlayerPair pair;
    public int secondsLeft;

    public InvitePair(PlayerPair pair, int seconds) {
        this.pair = pair;
        this.secondsLeft = seconds;
    }

    public static InvitePair of(ServerPlayerEntity challenger, ServerPlayerEntity opponent, int seconds) {
        return new InvitePair(new PlayerPair(challenger, opponent), seconds);
    }
}

