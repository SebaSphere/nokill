package dev.sebastianb.nokillkillkillkill.command.challenge;

import dev.sebastianb.nokillkillkillkill.command.challenge.pairstructs.InvitePair;
import dev.sebastianb.nokillkillkillkill.command.challenge.pairstructs.PlayerInviteList;
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

    public static void createInvite(ServerPlayerEntity from, ServerPlayerEntity to, int seconds) {
        playerInvites.add(InvitePair.of(from, to, seconds));
    }

    private static void progressInviteTimers() {
        for (var invite : playerInvites) {
            invite.secondsLeft -= 1;

            invite.pair.opponent().sendMessage(Text.literal(String.valueOf(invite.secondsLeft)), false);

            if (invite.secondsLeft <= 0) {
                playerInvites.remove(invite);

                invite.pair.challenger().sendMessage(Text.translatable(
                        "nokillkillkillkill.command.pvp.challenge.invite_expired_sent",
                        invite.pair.opponent().getName()
                ), false);
                invite.pair.opponent().sendMessage(Text.translatable(
                        "nokillkillkillkill.command.pvp.challenge.invite_expired_received",
                        invite.pair.challenger().getName()
                ), false);
            }
        }
    }

    private ChallengeInviteTimer() {}
}
