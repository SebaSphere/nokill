package dev.sebastianb.nokillkillkillkill.command.challenge.pairstructs;

import dev.sebastianb.nokillkillkillkill.command.challenge.PlayerPair;

public class InvitePair {
    public final PlayerPair pair;
    public int secondsLeft = 30;

    public InvitePair(PlayerPair pair) {
        this.pair = pair;
    }
}

