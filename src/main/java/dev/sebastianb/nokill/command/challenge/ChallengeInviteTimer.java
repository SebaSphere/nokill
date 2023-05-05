package dev.sebastianb.nokill.command.challenge;

import dev.sebastianb.nokill.command.challenge.pairstructs.InvitePair;
import dev.sebastianb.nokill.command.challenge.pairstructs.PlayerInviteList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public final class ChallengeInviteTimer {
    private static final Timer timer = new Timer();
    public static final PlayerInviteList playerInvites = new PlayerInviteList();

    static {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progressInviteTimers();
            }
        }, 0, 1000); // 1,000 ms
    }

    public static void createInvite(ServerPlayerEntity from, ServerPlayerEntity to) {
        var pair = new PlayerPair(from, to);
        playerInvites.add(new InvitePair(pair));
        // let invite receiver know how much time they have to respond.
        pair.opponent()
                .sendMessage(Text.translatable("nokill.command.pvp.challenge.inform_of_invite_time", InvitePair.SECONDS_PER_INVITE), false);
    }

    private static void progressInviteTimers() {
        for (var invite : playerInvites) {
            invite.secondsLeft -= 1;

            if (invite.secondsLeft <= 0) { // remove invite from list and let players know it expired
                playerInvites.remove(invite);

                invite.pair.challenger().sendMessage(Text.translatable(
                        "nokill.command.pvp.challenge.invite_expired_sent",
                        invite.pair.opponent().getName().getString()
                ), false);
                invite.pair.opponent().sendMessage(Text.translatable(
                        "nokill.command.pvp.challenge.invite_expired_received",
                        invite.pair.challenger().getName().getString()
                ), false);

                return;
            }

            if (invite.secondsLeft % 10 == 0) { // multiples of 5
                invite.pair.opponent()
                        .sendMessage(Text.translatable("nokill.command.pvp.challenge.seconds_left", invite.secondsLeft), false);
            } else if (invite.secondsLeft <= 5) {
                // show both players final countdown as an overlay
                var secondsLeftOverlay = Text.literal("§c" + invite.secondsLeft);
                invite.pair.challenger().sendMessage(secondsLeftOverlay, true);
                invite.pair.opponent().sendMessage(secondsLeftOverlay, true);
            }
        }
    }

    private ChallengeInviteTimer() {}
}
