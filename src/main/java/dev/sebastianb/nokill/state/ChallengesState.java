package dev.sebastianb.nokill.state;

import dev.sebastianb.nokill.NoKill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class ChallengesState extends PersistentState {
    private static final String KEY_PLAYERS_COMPOUND = "nokill.playerStates";
    private static final String KEY_CHALLENGE_ATTEMPTS_INT = "challengeAttempts";
    private static final String KEY_CHALLENGE_WINS_INT = "challengeWins";

    public final HashMap<UUID, PlayerState> playerStateMap = new HashMap<>();

    public static ChallengesState getServerState(MinecraftServer server) {
        var overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) throw new RuntimeException("The overworld does not exist!?");

        var stateManager = overworld.getPersistentStateManager();

        return stateManager.getOrCreate(ChallengesState::createFromNBT, ChallengesState::new, NoKill.MOD_ID);
    }

//    public static PlayerState getPlayerState(LivingEntity player) {
//        var server = player.world.getServer();
//        if (server == null) throw new RuntimeException("Player " + player + " has no server!?");
//
//        var state = getServerState(server);
//
//        return state.playerStateMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerState());
//    }

    /**
     * Will run the given consumer on the PlayerState for the given player, and then mark the
     * ChallengesState instance as dirty so the caller does not need to worry about it.
     */
    public static void doWithPlayerState(LivingEntity player, Consumer<PlayerState> func) {
        var server = player.getWorld().getServer();
        if (server == null) throw new RuntimeException("Player " + player + " has no server!?");
        var serverState = getServerState(server);

        var playerState = serverState.playerStateMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerState());
        func.accept(playerState);

        serverState.markDirty();
    }

    public static ChallengesState createFromNBT(NbtCompound nbt) {
        var state = new ChallengesState();

        var allPlayersComp = nbt.getCompound(KEY_PLAYERS_COMPOUND);
        for (var key : allPlayersComp.getKeys()) {
            var playerCompound = allPlayersComp.getCompound(key);
            var playerState = new PlayerState();

            playerState.challengeAttempts = playerCompound.getInt(KEY_CHALLENGE_ATTEMPTS_INT);
            playerState.challengeWins = playerCompound.getInt(KEY_CHALLENGE_WINS_INT);

            var uuid = UUID.fromString(key);
            state.playerStateMap.put(uuid, playerState);
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        var allPlayersComp = new NbtCompound();
        for (var id : playerStateMap.keySet()) {
            var playerData = playerStateMap.get(id);
            var playerCompound = new NbtCompound();

            playerCompound.putInt(KEY_CHALLENGE_ATTEMPTS_INT, playerData.challengeAttempts);
            playerCompound.putInt(KEY_CHALLENGE_WINS_INT, playerData.challengeWins);

            allPlayersComp.put(id.toString(), playerCompound);
        }
        nbt.put(KEY_PLAYERS_COMPOUND, allPlayersComp);

        return nbt;
    }
}
