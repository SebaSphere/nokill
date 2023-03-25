package dev.sebastianb.nokillkillkillkill.command.challenge;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// A thread to check if a player is currently waiting on a challenge invite
public class ChallengeInviteTimer implements Runnable {

    // made
    public static volatile ConcurrentHashMap<UUID, UUID> invitedPlayerAndSourcePlayerUUID = new ConcurrentHashMap<>(); // holds player UUID and challenger UUID

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void runChallengeSchedule(ServerPlayerEntity invitedPlayer, ServerPlayerEntity challengerPlayer, int maxSecondsAlive) {
        executor.scheduleAtFixedRate(new ChallengeInviteTimer(invitedPlayer, challengerPlayer, maxSecondsAlive, executor), 0, 1, TimeUnit.SECONDS);
    }

    private final AtomicInteger secondsAlive;
    private final ServerPlayerEntity invitedPlayer;

    private final ServerPlayerEntity challengerPlayer;
    private final UUID invitedPlayerUUID;
    private final UUID challengerPlayerUUID;

    public ChallengeInviteTimer(ServerPlayerEntity invitedPlayer, ServerPlayerEntity challengerPlayer, int maxSecondsAlive, ScheduledExecutorService executor) {
        this.invitedPlayer = invitedPlayer;
        this.challengerPlayer = challengerPlayer;
        invitedPlayerUUID = invitedPlayer.getUuid();
        this.challengerPlayerUUID = challengerPlayer.getUuid();
        this.secondsAlive = new AtomicInteger(maxSecondsAlive);
        invitedPlayerAndSourcePlayerUUID.putIfAbsent(this.invitedPlayerUUID, this.challengerPlayerUUID);
    }

    @Override
    public synchronized void run() {
        if (!invitedPlayerAndSourcePlayerUUID.containsKey(invitedPlayer.getUuid())) {
            executor.shutdown();
            return;
        }

        // TODO: make this display 15, 10, 5, 3, 2, 1 then expire. Also make it pretty + use a translatable
        invitedPlayer.sendMessage(Text.literal(secondsAlive.toString()), false);

        if (secondsAlive.decrementAndGet() < 0) {
            invitedPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_expired_received", challengerPlayer.getName()), false);
            challengerPlayer.sendMessage(Text.translatable("nokillkillkillkill.command.pvp.challenge.invite_expired_sent", invitedPlayer.getName()), false);
            invitedPlayerAndSourcePlayerUUID.remove(invitedPlayerUUID);
            executor.shutdown();
        }

    }

}
