package dev.sebastianb.nokill.command.challenge.pairstructs;

import dev.sebastianb.nokill.command.challenge.PlayerPair;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Optional;

public class PlayerPairList extends ArrayList<PlayerPair> {
    /**
     * Will get the pair this UUID is contained in, optional will be empty if such pair doesn't exist
     */
    public Optional<PlayerPair> find(ServerPlayerEntity player) {
        for (var pair : this)
            if (pair.contains(player)) return Optional.of(pair);

        return Optional.empty();
    }

    /**
     * Returns true if the given UUID is in any challenge currently (in playerPairs)
     */
    public boolean contains(ServerPlayerEntity player) {
        return find(player).isPresent();
    }
}
