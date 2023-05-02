package dev.sebastianb.nokill.command.challenge.pairstructs;

import dev.sebastianb.nokill.command.challenge.PlayerPair;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Optional;

public class PlayerPairList extends ArrayList<PlayerPair> {
    /** @return an Optional of a PlayerPair containing the given player */
    public Optional<PlayerPair> find(ServerPlayerEntity player) {
        for (var pair : this)
            if (pair.contains(player)) return Optional.of(pair);

        return Optional.empty();
    }
}
