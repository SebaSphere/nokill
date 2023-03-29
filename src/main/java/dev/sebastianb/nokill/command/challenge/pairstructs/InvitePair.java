package dev.sebastianb.nokill.command.challenge.pairstructs;

import dev.sebastianb.nokill.command.challenge.PlayerPair;

public class InvitePair {
    public final PlayerPair pair;
    public int secondsLeft = 30;

    public InvitePair(PlayerPair pair) {
        this.pair = pair;
    }
}

