package dev.sebastianb.nokill.command.challenge.pairstructs;

import dev.sebastianb.nokill.command.challenge.PlayerPair;

public class InvitePair {
    public static final int SECONDS_PER_INVITE = 30;

    public final PlayerPair pair;
    public int secondsLeft = SECONDS_PER_INVITE;

    public InvitePair(PlayerPair pair) {
        this.pair = pair;
    }
}

