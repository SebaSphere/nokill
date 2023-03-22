package dev.sebastianb.nokillkillkillkill.command.challenge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// A thread to check if a player is currently waiting on a challenge invite
public class ChallengeInviteTimer implements Runnable {
    public static volatile ConcurrentHashMap<UUID, UUID> invitedPlayerAndFactionUUID = new ConcurrentHashMap<>(); // holds player UUID and faction UUID

    private final ScheduledExecutorService executor;

    public static void runThread(ServerPlayerEntity invitedPlayer, ServerPlayerEntity challengerPlayer, int maxSecondsAlive) {
        ThreadFactory namedThreadFactory =
                new ThreadFactoryBuilder().setNameFormat(invitedPlayer.getUuid() + "-%d").build();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3, namedThreadFactory); // TODO: Make a config option for amount of threads

        executor.scheduleAtFixedRate(new ChallengeInviteTimer(invitedPlayer, challengerPlayer, maxSecondsAlive, executor), 0, 1, TimeUnit.SECONDS);
    }


    private final AtomicInteger secondsAlive;
    private final ServerPlayerEntity invitedPlayer;
    private final UUID invitedPlayerUUID;
    private final UUID challengerPlayer;

    public ChallengeInviteTimer(ServerPlayerEntity invitedPlayer, ServerPlayerEntity challengerPlayer, int maxSecondsAlive, ScheduledExecutorService executor) {
        this.invitedPlayer = invitedPlayer;
        invitedPlayerUUID = invitedPlayer.getUuid();
        this.challengerPlayer = challengerPlayer.getUuid();
        this.secondsAlive = new AtomicInteger(maxSecondsAlive);
        this.executor = executor;
        invitedPlayerAndFactionUUID.putIfAbsent(this.challengerPlayer, this.challengerPlayer);
    }

    @Override
    public synchronized void run() {
        if (!invitedPlayerAndFactionUUID.containsKey(invitedPlayer.getUuid())) {
            executor.shutdown();
            return;
        }

        // TODO: make this display 15, 10, 5, 3, 2, 1 then expire. Also make it pretty + use a translatable
        invitedPlayer.sendMessage(Text.literal(secondsAlive.toString()), false);

        if (secondsAlive.decrementAndGet() < 0) {
            invitedPlayer.sendMessage(Text.literal("Faction invite expired"), false); // TODO: replace with translatable
            invitedPlayerAndFactionUUID.remove(invitedPlayerUUID);
            executor.shutdown();
        }

    }

}
